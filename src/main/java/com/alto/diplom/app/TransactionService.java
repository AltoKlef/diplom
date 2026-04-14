package com.alto.diplom.app;

import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.transactions.Transaction;
import io.jmix.core.DataManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final DataManager dataManager;

    public void createWelcomeTransaction(Customer customer, BigDecimal amount, Integer delay) {
        Transaction transaction = dataManager.create(Transaction.class);
        transaction.setCustomer(customer);
        transaction.setCompany(customer.getCompany());
        transaction.setExternalNumber("WELCOME-" + customer.getPhone());
        transaction.setMarksEarned(amount);
        transaction.setTotalAmount(BigDecimal.ZERO);

        if (delay > 0) {
            transaction.setIsNeedToActivate(true);
            transaction.setTimeActivate(OffsetDateTime.now().plusDays(delay));
        } else {
            transaction.setIsNeedToActivate(false);
            transaction.setTimeActivate(OffsetDateTime.now());
        }

        dataManager.save(transaction);
    }
}
