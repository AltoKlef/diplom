package com.alto.diplom.repository;

import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.core.Customer;
import io.jmix.core.repository.JmixDataRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JmixDataRepository<Customer, UUID> {
    Optional<Customer> findByPhoneAndCompany(String phone, Company company);
}