package com.alto.diplom.listeners.registration;


import com.alto.diplom.core.services.CompanyInitializerService;
import com.alto.diplom.entity.core.Company;
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
    @Autowired
    private CompanyInitializerService companyInitializerService;
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onCompanyCreated(EntityChangedEvent<Company> event) {

        if (event.getType() == EntityChangedEvent.Type.CREATED) {
            Company company = dataManager.load(event.getEntityId()).one();
            companyInitializerService.initNewCompany(company);
        }

    }
}