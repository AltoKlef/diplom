package com.alto.diplom.app;

import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import com.alto.diplom.entity.items.Item;
import com.alto.diplom.entity.transactions.Transaction;
import com.alto.diplom.entity.transactions.TransactionItem;
import com.alto.diplom.repository.CustomerBonusAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LoyaltyService {

    @Autowired
    private CustomerBonusAccountRepository accountRepository;

    /**
     * Основной метод расчета начисления
     */
    public void calculateAccrual(Transaction transaction) {
        CustomerBonusAccount account = accountRepository
                .findByCustomerAndCompany(transaction.getCustomer(), transaction.getCompany())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        LoyaltyLevel currentLevel = account.getLoyaltyLevel();

        if (currentLevel == null) return;

        BigDecimal totalEarned = BigDecimal.ZERO;

        // 2. Итерируемся по позициям чека
        for (TransactionItem line : transaction.getItems()) {
            Item item = line.getItem();

            // Проверяем, можно ли на этот товар начислять баллы
            if (Boolean.TRUE.equals(item.getCanMarkIncrease())) {
                // Формула: (Цена * Кол-во) * (Процент кэшбека из уровня / 100)
                BigDecimal earnedForLine = line.getTotalSum()
                        .multiply(currentLevel.getCashbackRate())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                line.setMarksEarned(earnedForLine);
                totalEarned = totalEarned.add(earnedForLine);
            } else {
                line.setMarksEarned(BigDecimal.ZERO);
            }
        }

        // 3. Записываем итого в "голову" транзакции
        transaction.setMarksEarned(totalEarned);
    }

}