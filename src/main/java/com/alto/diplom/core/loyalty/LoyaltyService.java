package com.alto.diplom.core.loyalty;

import com.alto.diplom.entity.MarkIncreaseMode;
import com.alto.diplom.entity.MarkStrategy;
import com.alto.diplom.entity.TransactionConfig;
import com.alto.diplom.entity.TransactionParameters;
import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.core.CustomerGroup;
import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.entity.transactions.TransactionItem;
import com.alto.diplom.repository.CustomerBonusAccountRepository;
import com.alto.diplom.repository.LoyaltyLevelRepository;
import com.alto.diplom.repository.LoyaltyProgramConfigRepository;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alto.diplom.entity.TransactionParameters.CalculationScenario;
import com.alto.diplom.entity.TransactionParameters.ItemResult;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
// ... остальные импорты

@Service
public class LoyaltyService {

    private static final Logger log = LoggerFactory.getLogger(LoyaltyService.class);

    @Autowired
    private CustomerBonusAccountRepository accountRepository;
    @Autowired
    private LoyaltyProgramConfigRepository loyaltyConfigRepository;
    @Autowired
    private LoyaltyLevelRepository levelRepository;
    @Autowired
    private DataManager dataManager;

    /**
     * Основной метод для получения всех параметров расчета (сценарии 0 и MAX)
     */
    public TransactionParameters calculateFullParameters(Transaction transaction) {
        log.info(">>> Начало полного расчета лояльности для транзакции: {}", transaction.getExternalNumber());

        if (transaction.getCustomer() == null) {
            log.warn("Расчет невозможен: клиент не указан");
            throw new RuntimeException("Клиент не выбран");
        }

        TransactionParameters params = new TransactionParameters();

        CustomerBonusAccount account = accountRepository
                .findByCustomerAndCompany(transaction.getCustomer(), transaction.getCompany())
                .orElseThrow(() -> new RuntimeException("Бонусный счет клиента не найден"));

        params.setCustomer(transaction.getCustomer());
        params.setBalanceBefore(account.getMark());

        LoyaltyProgramConfig bestConfig = findBestConfig(transaction.getCustomer(), transaction.getCompany());
        log.debug("Выбран конфиг: {} (ID: {})", bestConfig.getName(), bestConfig.getId());

        LoyaltyLevel currentLevel = syncAndGetActualLevel(account, bestConfig);

        // Получаем настройки транзакции из программы
        TransactionConfig txConfig = bestConfig.getTransactionConfig();
        if (txConfig == null) {
            throw new RuntimeException("В программе лояльности не задан TransactionConfig");
        }

        // 4. Сценарий 1: Zero Spend (чистое начисление)
        params.setZeroSpendScenario(calculateScenario(transaction, currentLevel, BigDecimal.ZERO, txConfig));

        // 5. Сценарий 2: Max Spend
        BigDecimal maxToSpend = calculateMaxPossibleSpend(transaction, currentLevel, account.getMark());
        params.setMaxSpendScenario(calculateScenario(transaction, currentLevel, maxToSpend, txConfig));

        return params;
    }

    private LoyaltyProgramConfig findBestConfig(Customer customer, Company company) {
        // ВНИМАНИЕ: Убедись, что метод findActiveConfigs загружает transactionConfig
        // через fetchPlan (иначе будет LazyInitializationException)
        List<LoyaltyProgramConfig> activeConfigs = loyaltyConfigRepository.findActiveConfigs(company);

        Set<UUID> customerGroupIds = customer.getCustomerGroups() != null
                ? customer.getCustomerGroups().stream().map(CustomerGroup::getId).collect(Collectors.toSet())
                : Collections.emptySet();

        return activeConfigs.stream()
                .filter(config -> {
                    if (config.getCustomerGroup() == null) return true;
                    return customerGroupIds.contains(config.getCustomerGroup().getId());
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Подходящая программа лояльности не найдена"));
    }

    /**
     * Универсальное ядро расчета сценария с учетом TransactionConfig
     */
    private CalculationScenario calculateScenario(Transaction transaction, LoyaltyLevel level, BigDecimal spendAmount, TransactionConfig txConfig) {
        CalculationScenario scenario = new CalculationScenario();
        scenario.setMarksToSpend(spendAmount);
        scenario.setItemResults(new ArrayList<>());

        BigDecimal totalEarned = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal currentSpendLeft = spendAmount;

        BigDecimal cashbackRate = level.getCashbackRate();
        BigDecimal levelDiscountRate = level.getDiscount();

        // === ПРОВЕРКА ПРАВИЛ ИЗ КОНФИГА ===
        boolean canEarnPoints = true;

        // Правило 1: Минимальная сумма чека для начисления
        if (txConfig.getMinSumToIncrease() != null) {
            BigDecimal minSum = new BigDecimal(txConfig.getMinSumToIncrease());
            if (transaction.getTotalAmount().compareTo(minSum) < 0) {
                canEarnPoints = false;
                log.debug("Сумма чека меньше минимальной ({} < {}). Начисление отменено.", transaction.getTotalAmount(), minSum);
            }
        }

        // Правило 2: Режим начисления при списании
        if (spendAmount.compareTo(BigDecimal.ZERO) > 0 && txConfig.getMarkIncreaseMode() == MarkIncreaseMode.CAN_NOT_INCREASE) {
            canEarnPoints = false;
            log.debug("Выбрано списание баллов, а режим CAN_NOT_INCREASE активен. Начисление отменено.");
        }
        // ===================================

        for (TransactionItem item : transaction.getItems()) {
            if (item.getItem() == null || item.getTotalSum() == null) continue;

            ItemResult itemRes = new ItemResult();
            itemRes.setTransactionItem(item);

            BigDecimal directDiscount = item.getTotalSum()
                    .multiply(levelDiscountRate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal priceAfterDiscount = item.getTotalSum().subtract(directDiscount);

            BigDecimal marksSpentOnItem = BigDecimal.ZERO;
            if (currentSpendLeft.compareTo(BigDecimal.ZERO) > 0 && Boolean.TRUE.equals(item.getItem().getCanPayByMark())) {
                marksSpentOnItem = priceAfterDiscount.min(currentSpendLeft);
                currentSpendLeft = currentSpendLeft.subtract(marksSpentOnItem);
            }

            BigDecimal earned = BigDecimal.ZERO;
            // Начисляем только если разрешено конфигом И товаром
            if (canEarnPoints && Boolean.TRUE.equals(item.getItem().getCanMarkIncrease())) {
                BigDecimal finalCashPart = priceAfterDiscount.subtract(marksSpentOnItem);
                earned = finalCashPart
                        .multiply(cashbackRate)
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            }

            itemRes.setDiscount(directDiscount);
            itemRes.setMarksSpent(marksSpentOnItem);
            itemRes.setMarksEarned(earned);

            totalEarned = totalEarned.add(earned);
            totalDiscount = totalDiscount.add(directDiscount).add(marksSpentOnItem);

            scenario.getItemResults().add(itemRes);
        }

        scenario.setMarksToEarn(totalEarned);
        scenario.setTotalDiscount(totalDiscount);
        scenario.setTotalAmount(transaction.getTotalAmount().subtract(totalDiscount));

        return scenario;
    }

    private BigDecimal calculateMaxPossibleSpend(Transaction transaction, LoyaltyLevel level, BigDecimal customerBalance) {
        BigDecimal spendRate = level.getMarkspendRate();
        BigDecimal levelDiscountRate = level.getDiscount();

        BigDecimal maxLimitByItems = transaction.getItems().stream()
                .filter(i -> i.getItem() != null && Boolean.TRUE.equals(i.getItem().getCanPayByMark()))
                .map(i -> {
                    BigDecimal afterDiscount = i.getTotalSum().subtract(
                            i.getTotalSum().multiply(levelDiscountRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                    );
                    return afterDiscount.multiply(spendRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return customerBalance.min(maxLimitByItems);
    }

    public LoyaltyLevel syncAndGetActualLevel(CustomerBonusAccount account, LoyaltyProgramConfig config) {
        BigDecimal spent = account.getEffectiveCash() != null ? account.getEffectiveCash() : BigDecimal.ZERO;

        LoyaltyLevel actualLevel = levelRepository.findApplicableLevels(config, spent).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("В конфигурации лояльности не найдены уровни"));

        if (!actualLevel.equals(account.getLoyaltyLevel())) {
            account.setLoyaltyLevel(actualLevel);
        }

        return actualLevel;
    }

    @Transactional
    public void executeTransaction(Transaction transaction) {
        log.info(">>> Запуск проведения транзакции: {}", transaction.getExternalNumber());

        CustomerBonusAccount account = accountRepository
                .findByCustomerAndCompany(transaction.getCustomer(), transaction.getCompany())
                .orElseThrow(() -> new RuntimeException("Бонусный счет клиента не найден"));

        // Получаем конфиг для заполнения полей активации чека
        LoyaltyProgramConfig bestConfig = findBestConfig(transaction.getCustomer(), transaction.getCompany());
        TransactionConfig txConfig = bestConfig.getTransactionConfig();

        BigDecimal spent = transaction.getMarksSpent() != null ? transaction.getMarksSpent() : BigDecimal.ZERO;
        BigDecimal earned = transaction.getMarksEarned() != null ? transaction.getMarksEarned() : BigDecimal.ZERO;

        // === 1. УСТАНОВКА СТРАТЕГИИ ТРАНЗАКЦИИ ===
        if (spent.compareTo(BigDecimal.ZERO) > 0) {
            transaction.setMarkStrategy(MarkStrategy.SPENDING);
        } else if (earned.compareTo(BigDecimal.ZERO) > 0) {
            transaction.setMarkStrategy(MarkStrategy.EARNING);
        } else {
            transaction.setMarkStrategy(MarkStrategy.NONE);
        }

        // === 2. УСТАНОВКА АКТИВАЦИИ БАЛЛОВ ===
        if (txConfig.getDaysToMarkActivation() != null && txConfig.getDaysToMarkActivation() > 0) {
            transaction.setIsNeedToActivate(true);
            transaction.setTimeActivate(OffsetDateTime.now().plusDays(txConfig.getDaysToMarkActivation()));
        } else {
            transaction.setIsNeedToActivate(false);
            transaction.setTimeActivate(OffsetDateTime.now());
        }

        // === 3. ИЗМЕНЕНИЕ БАЛАНСА БАЛЛОВ ===
        BigDecimal currentMarks = account.getMark() != null ? account.getMark() : BigDecimal.ZERO;
        BigDecimal newBalance;
        if (Boolean.TRUE.equals(transaction.getIsNeedToActivate())) {
            newBalance = currentMarks.subtract(spent);
        } else {
            newBalance = currentMarks.subtract(spent).add(earned);
        }

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Недостаточно баллов на счету.");
        }
        account.setMark(newBalance);

        // === 4. ОБНОВЛЕНИЕ СУММЫ НАКОПЛЕНИЙ (EFFECTIVE CASH) ===
        BigDecimal currentEffectiveCash = account.getEffectiveCash() != null ? account.getEffectiveCash() : BigDecimal.ZERO;
        // Берем финальную сумму чека (после всех скидок и списаний)
        BigDecimal finalPaidAmount = transaction.getTotalAmount() != null ? transaction.getTotalAmount() : BigDecimal.ZERO;

        BigDecimal newEffectiveCash = currentEffectiveCash.add(finalPaidAmount);
        account.setEffectiveCash(newEffectiveCash);


        syncAndGetActualLevel(account, bestConfig);

        // === 6. СОХРАНЕНИЕ ===
        dataManager.save(transaction, account);

        log.info("Транзакция ID: {} проведена (Стратегия: {}). Новый баланс: {}, Эффективная сумма: {}",
                transaction.getId(), transaction.getMarkStrategy().name(), newBalance, newEffectiveCash);
    }
}