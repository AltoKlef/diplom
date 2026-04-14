package com.alto.diplom.repository;

import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import io.jmix.core.repository.FetchPlan;
import io.jmix.core.repository.JmixDataRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerBonusAccountRepository extends JmixDataRepository<CustomerBonusAccount, UUID> {

    Optional<CustomerBonusAccount> findByCustomerAndCompany(Customer customer, Company company);

    @FetchPlan("_base")
    Optional<CustomerBonusAccount> findOptionalByCustomerAndCompany(Customer customer, Company company);
}