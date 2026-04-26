package com.alto.diplom.core.loyalty;

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
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        // 1. Получаем аккаунт
        CustomerBonusAccount account = accountRepository
                .findByCustomerAndCompany(transaction.getCustomer(), transaction.getCompany())
                .orElseThrow(() -> new RuntimeException("Бонусный счет клиента не найден"));

        params.setCustomer(transaction.getCustomer());
        params.setBalanceBefore(account.getMark());

        // 2. Ищем лучший конфиг
        LoyaltyProgramConfig bestConfig = findBestConfig(transaction.getCustomer(), transaction.getCompany());
        log.debug("Выбран конфиг: {} (ID: {})", bestConfig.getName(), bestConfig.getId());

        // 3. СИНХРОНИЗАЦИЯ: Обновляем уровень клиента согласно выбранному конфигу
        LoyaltyLevel currentLevel = syncAndGetActualLevel(account, bestConfig);
        log.info("Текущий уровень клиента: {} (Cashback: {}%)", currentLevel.getName(), currentLevel.getCashbackRate());

        // 4. Сценарий 1: Zero Spend (чистое начисление)
        params.setZeroSpendScenario(calculateScenario(transaction, currentLevel, BigDecimal.ZERO));

        // 5. Сценарий 2: Max Spend
        BigDecimal maxToSpend = calculateMaxPossibleSpend(transaction, currentLevel, account.getMark());
        params.setMaxSpendScenario(calculateScenario(transaction, currentLevel, maxToSpend));

        log.info("Расчет завершен. Макс. списание: {}, Начисление при этом: {}",
                maxToSpend, params.getMaxSpendScenario().getMarksToEarn());

        return params;
    }

    /**
     * Поиск конфига по приоритету и группам
     */
    private LoyaltyProgramConfig findBestConfig(Customer customer, Company company) {
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
     * Универсальное ядро расчета сценария
     */
    private CalculationScenario calculateScenario(Transaction transaction, LoyaltyLevel level, BigDecimal spendAmount) {
        CalculationScenario scenario = new CalculationScenario();
        scenario.setMarksToSpend(spendAmount);
        scenario.setItemResults(new ArrayList<>());

        BigDecimal totalEarned = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal currentSpendLeft = spendAmount;

        BigDecimal cashbackRate = level.getCashbackRate();
        BigDecimal levelDiscountRate = level.getDiscount();

        for (TransactionItem item : transaction.getItems()) {
            if (item.getItem() == null || item.getTotalSum() == null) continue;

            ItemResult itemRes = new ItemResult();
            itemRes.setTransactionItem(item);

            // 1. Скидка уровня
            BigDecimal directDiscount = item.getTotalSum()
                    .multiply(levelDiscountRate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal priceAfterDiscount = item.getTotalSum().subtract(directDiscount);

            // 2. Списание баллов
            BigDecimal marksSpentOnItem = BigDecimal.ZERO;
            if (currentSpendLeft.compareTo(BigDecimal.ZERO) > 0 && Boolean.TRUE.equals(item.getItem().getCanPayByMark())) {
                marksSpentOnItem = priceAfterDiscount.min(currentSpendLeft);
                currentSpendLeft = currentSpendLeft.subtract(marksSpentOnItem);
            }

            // 3. Начисление баллов
            BigDecimal finalCashPart = priceAfterDiscount.subtract(marksSpentOnItem);
            BigDecimal earned = BigDecimal.ZERO;
            if (Boolean.TRUE.equals(item.getItem().getCanMarkIncrease())) {
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

    /**
     * Расчет максимально допустимого списания по чеку
     */
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

    /**
     * Синхронизация уровня клиента на основе его накопленных трат
     */
    public LoyaltyLevel syncAndGetActualLevel(CustomerBonusAccount account, LoyaltyProgramConfig config) {
        BigDecimal spent = account.getEffectiveCash() != null ? account.getEffectiveCash() : BigDecimal.ZERO;

        LoyaltyLevel actualLevel = levelRepository.findApplicableLevels(config, spent).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("В конфигурации лояльности не найдены уровни"));

        if (!actualLevel.equals(account.getLoyaltyLevel())) {
            log.info(">>> Уровень клиента изменился! Старый: {}, Новый: {}",
                    account.getLoyaltyLevel() != null ? account.getLoyaltyLevel().getName() : "нет",
                    actualLevel.getName());
            account.setLoyaltyLevel(actualLevel);
        }

        return actualLevel;
    }
}