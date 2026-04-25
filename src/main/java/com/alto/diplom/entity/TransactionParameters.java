package com.alto.diplom.entity;

import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.transactions.TransactionItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class TransactionParameters {

    // Сценарий 1: Если клиент НЕ спитсывает баллы
    private CalculationScenario zeroSpendScenario;

    // Сценарий 2: Если клиент списывает МАКСИМУМ баллов
    private CalculationScenario maxSpendScenario;

    // Общие данные контекста
    private Customer customer;
    private BigDecimal balanceBefore; // Баланс ДО покупки
    @Getter
    @Setter
    public static class CalculationScenario {
        private BigDecimal totalAmount;       // Итоговая сумма к оплате "деньгами"
        private BigDecimal totalDiscount;     // Сумма скидок
        private BigDecimal marksToEarn;       // Сколько будет начислено
        private BigDecimal marksToSpend;      // Сколько будет списано

        // Список результатов по каждой строке чека
        private List<ItemResult> itemResults;
    }
    @Getter
    @Setter
    public static class ItemResult {
        private TransactionItem transactionItem; // Ссылка на саму сущность
        private BigDecimal marksEarned;
        private BigDecimal marksSpent;
        private BigDecimal discount;
    }
}