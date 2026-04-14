package com.alto.diplom.app;

import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import com.alto.diplom.repository.LoyaltyLevelRepository;
import io.jmix.core.DataManager;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CustomerInsertListener {
    @Autowired
    private DataManager dataManager;
    @Autowired
    private LoyaltyLevelRepository levelRepository;

    @EventListener
    public void onCustomerCreated(EntityChangedEvent<Customer> event) {
        // Срабатывает только после вставки (Created) в БД
        if (event.getType() == EntityChangedEvent.Type.CREATED) {

            // Идем в БД за полной сущностью (ивент шлет только Id)
            Id<Customer> customerId = event.getEntityId();
            Customer customer = dataManager.load(customerId).one();

            // Создаем счет
            CustomerBonusAccount account = dataManager.create(CustomerBonusAccount.class);
            account.setCustomer(customer);
            account.setCompany(customer.getCompany());
            //TODO
            account.setMark(BigDecimal.ZERO);

            // Устанавливаем уровень
            levelRepository.findDefaultLevel(customer.getCompany())
                    .ifPresent(account::setLoyaltyLevel);

            dataManager.save(account);
        }
    }
}