package com.alto.diplom.core.services;

import com.alto.diplom.core.config.CompanyDefaults;
import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.items.ItemGroup;
import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CompanyInitializerService {

    @Autowired
    private DataManager dataManager;

    @Transactional(propagation = Propagation.MANDATORY) // Должен быть в транзакции лисенера
    public void initNewCompany(Company company) {
        createDefaultCatalog(company);
        createDefaultLoyaltyProgram(company);
    }

    private void createDefaultCatalog(Company company) {
        ItemGroup catalog = dataManager.create(ItemGroup.class);
        catalog.setName(CompanyDefaults.DEFAULT_CATALOG_NAME);
        catalog.setCompany(company);
        dataManager.save(catalog);
    }

    private void createDefaultLoyaltyProgram(Company company) {
        LoyaltyProgramConfig config = dataManager.create(LoyaltyProgramConfig.class);
        config.setCompany(company);
        config.setName(CompanyDefaults.DEFAULT_LOYALTY_NAME);
        config.setIsActive(true);
        config.setPriority(CompanyDefaults.DEFAULT_PRIORITY);

        LoyaltyLevel level = dataManager.create(LoyaltyLevel.class);
        level.setCashbackRate(CompanyDefaults.DEFAULT_CASHBACK);
        level.setMarkspendRate(CompanyDefaults.DEFAULT_MARK_SPEND);
        level.setLoyaltyProgramConfig(config);
        level.setNumber(CompanyDefaults.DEFAULT_LEVEL_NUMBER);
        level.setName(CompanyDefaults.DEFAULT_LEVEL_NAME);

        dataManager.save(config, level);
    }
}