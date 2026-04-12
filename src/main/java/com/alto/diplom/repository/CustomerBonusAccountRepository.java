package com.alto.diplom.repository;

import com.alto.diplom.entity.loyalty.CustomerBonusAccount;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface CustomerBonusAccountRepository extends JmixDataRepository<CustomerBonusAccount, UUID> {
}