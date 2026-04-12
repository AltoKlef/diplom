package com.alto.diplom.repository;

import com.alto.diplom.core.Customer;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface CustomerRepository extends JmixDataRepository<Customer, UUID> {
}