package com.alto.diplom.repository;

import com.alto.diplom.entity.config.OnRegisterConfig;
import com.alto.diplom.entity.core.Company;
import io.jmix.core.repository.JmixDataRepository;

import java.util.Optional;
import java.util.UUID;

public interface OnRegisterConfigRepository extends JmixDataRepository<OnRegisterConfig, UUID> {

    Optional<OnRegisterConfig> findByCompanyAndIsActiveTrue(Company company);
}