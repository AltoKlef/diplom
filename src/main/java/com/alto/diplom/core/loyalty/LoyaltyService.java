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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alto.diplom.entity.TransactionParameters.CalculationScenario;
import com.alto.diplom.entity.TransactionParameters.ItemResult;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoyaltyService {

    @Autowired
    private CustomerBonusAccountRepository accountRepository;

    @Autowired
    private LoyaltyProgramConfigRepository loyaltyConfigRepository;

    @Autowired
    private LoyaltyLevelRepository levelRepository;

    /**
     * Основной метод расчета начисления
     */
    public void calculateAccrual(Transaction transaction) {
        System.out.println(">>> LoyaltyService: Начинаю расчет для транзакции № " + transaction.getExternalNumber());
        if (transaction.getCustomer() == null || transaction.getCompany() == null) return;

        CustomerBonusAccount account = accountRepository
                .findByCustomerAndCompany(transaction.getCustomer(), transaction.getCompany())
                .orElse(null); // Не кидаем ошибку сразу, просто выходим

        if (account == null || account.getLoyaltyLevel() == null) return;

        LoyaltyLevel currentLevel = account.getLoyaltyLevel();
        BigDecimal totalEarned = BigDecimal.ZERO;

        if (transaction.getItems() == null) return;

        for (TransactionItem line : transaction.getItems()) {
            // Если товар еще не выбран в строке - пропускаем её
            if (line.getItem() == null || line.getTotalSum() == null) {
                line.setMarksEarned(BigDecimal.ZERO);
                continue;
            }

            if (Boolean.TRUE.equals(line.getItem().getCanMarkIncrease())) {
                BigDecimal earnedForLine = line.getTotalSum()
                        .multiply(currentLevel.getCashbackRate())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                line.setMarksEarned(earnedForLine);
                totalEarned = totalEarned.add(earnedForLine);
            } else {
                line.setMarksEarned(BigDecimal.ZERO);
            }
        }
        transaction.setMarksEarned(totalEarned);
    }

    public TransactionParameters calculateFullParameters(Transaction transaction) {
        TransactionParameters params = new TransactionParameters();

        // 1. Получаем аккаунт через DAO
        CustomerBonusAccount account = accountRepository
                .findByCustomerAndCompany(transaction.getCustomer(), transaction.getCompany())
                .orElseThrow(() -> new RuntimeException("Бонусный счет не найден"));

        params.setCustomer(transaction.getCustomer());
        params.setBalanceBefore(account.getMark());

        // 2. Получаем конфиг (логика фильтрации по группам остается в сервисе)
        LoyaltyProgramConfig bestConfig = findBestConfig(transaction.getCustomer(), transaction.getCompany());

        // 3. Сценарии
        params.setZeroSpendScenario(calculateScenario(transaction, account, BigDecimal.ZERO));

        BigDecimal maxToSpend = calculateMaxPossibleSpend(transaction, account);
        params.setMaxSpendScenario(calculateScenario(transaction, account, maxToSpend));

        return params;
    }

    private LoyaltyProgramConfig findBestConfig(Customer customer, Company company) {
        // 1. Достаем все активные конфиги компании, отсортированные по приоритету
        List<LoyaltyProgramConfig> activeConfigs = loyaltyConfigRepository.findActiveConfigs(company);

        // 2. Получаем ID групп клиента
        Set<UUID> customerGroupIds = customer.getCustomerGroups().stream()
                .map(CustomerGroup::getId)
                .collect(Collectors.toSet());

        // 3. Ищем самый приоритетный подходящий
        return activeConfigs.stream()
                .filter(config -> {
                    // Если группа в конфиге не указана (null) — подходит всем
                    if (config.getCustomerGroup() == null) {
                        return true;
                    }
                    // Иначе проверяем, есть ли эта конкретная группа у клиента
                    return customerGroupIds.contains(config.getCustomerGroup().getId());
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Подходящая программа лояльности не найдена"));
    }


    private CalculationScenario calculateScenario(Transaction transaction, CustomerBonusAccount account, BigDecimal spendAmount) {
        CalculationScenario scenario = new CalculationScenario();
        scenario.setMarksToSpend(spendAmount);
        scenario.setItemResults(new ArrayList<>());

        BigDecimal totalEarned = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal currentSpendLeft = spendAmount;

        LoyaltyLevel level = account.getLoyaltyLevel();
        BigDecimal cashbackRate = (level != null) ? level.getCashbackRate() : BigDecimal.ZERO;
        BigDecimal levelDiscountRate = (level != null) ? level.getDiscount() : BigDecimal.ZERO;

        for (TransactionItem item : transaction.getItems()) {
            ItemResult itemRes = new ItemResult();
            itemRes.setTransactionItem(item);

            // --- ШАГ 1: Прямая скидка уровня (если есть) ---
            BigDecimal directDiscount = item.getTotalSum()
                    .multiply(levelDiscountRate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            BigDecimal priceAfterDiscount = item.getTotalSum().subtract(directDiscount);

            // --- ШАГ 2: Списание баллов (Marks Spent) ---
            BigDecimal marksSpentOnItem = BigDecimal.ZERO;
            if (currentSpendLeft.compareTo(BigDecimal.ZERO) > 0 && Boolean.TRUE.equals(item.getItem().getCanPayByMark())) {
                marksSpentOnItem = priceAfterDiscount.min(currentSpendLeft);
                currentSpendLeft = currentSpendLeft.subtract(marksSpentOnItem);
            }

            // --- ШАГ 3: Начисление баллов (Marks Earned) ---
            // База для начисления = Цена - Прямая Скидка - Списанные баллы
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

    private BigDecimal calculateMaxPossibleSpend(Transaction transaction, CustomerBonusAccount account) {
        if (account.getLoyaltyLevel() == null) return BigDecimal.ZERO;

        BigDecimal spendRate = account.getLoyaltyLevel().getMarkspendRate();
        BigDecimal levelDiscountRate = account.getLoyaltyLevel().getDiscount();

        BigDecimal maxLimitByItems = transaction.getItems().stream()
                .filter(i -> Boolean.TRUE.equals(i.getItem().getCanPayByMark()))
                .map(i -> {
                    // Сначала вычитаем прямую скидку, потом считаем лимит списания от остатка
                    BigDecimal afterDiscount = i.getTotalSum().subtract(
                            i.getTotalSum().multiply(levelDiscountRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                    );
                    return afterDiscount.multiply(spendRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return account.getMark().min(maxLimitByItems);
    }

    /**
     * Определяет актуальный уровень и обновляет его в аккаунте, если он изменился.
     */
    public LoyaltyLevel syncAndGetActualLevel(CustomerBonusAccount account, LoyaltyProgramConfig config) {
        BigDecimal spent = account.getEffectiveCash() != null ? account.getEffectiveCash() : BigDecimal.ZERO;

        // Ищем, какой уровень сейчас подходит клиенту по его тратам
        LoyaltyLevel actualLevel = levelRepository.findApplicableLevels(config, spent).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("В программе лояльности не настроены уровни"));

        // Если в базе записан другой уровень — обновляем (кэшируем)
        if (!actualLevel.equals(account.getLoyaltyLevel())) {
            account.setLoyaltyLevel(actualLevel);
            // Мы не вызываем dataManager.save(account) здесь,
            // чтобы не делать лишних транзакций.
            // Объект обновится, когда мы сохраним всю транзакцию покупки.
        }

        return actualLevel;
    }
}