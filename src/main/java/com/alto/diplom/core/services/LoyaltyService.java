package com.alto.diplom.core.services;

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
        // Важно: проверяем на null, так как во вьюхе юзер может еще не выбрать клиента
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
}