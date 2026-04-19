package com.alto.diplom.listeners.registration;


import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.items.ItemGroup;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntityChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class CompanyRegistrationListener {

    @Autowired
    private DataManager dataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCompanyCreated(EntityChangedEvent<Company> event) {

        // Нас интересует только момент ПЕРВОГО создания
        if (event.getType() == EntityChangedEvent.Type.CREATED) {

            // 1. Подгружаем саму компанию (из события получаем её Id)
            Company company = dataManager.load(event.getEntityId()).one();

            // 2. Создаем дефолтную группу товаров (каталог)
            ItemGroup defaultCatalog = dataManager.create(ItemGroup.class);
            defaultCatalog.setName("Основной каталог"); // Название по умолчанию
            defaultCatalog.setCompany(company);

            // 3. Сохраняем
            dataManager.save(defaultCatalog);
        }
    }
}