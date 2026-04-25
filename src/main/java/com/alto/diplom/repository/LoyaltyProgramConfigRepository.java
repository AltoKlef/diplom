package com.alto.diplom.repository;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.core.Company;
import io.jmix.core.repository.JmixDataRepository;
import io.jmix.core.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LoyaltyProgramConfigRepository extends JmixDataRepository<LoyaltyProgramConfig, UUID> {
    @Query("select c from LoyaltyProgramConfig c " +
            "where c.company = :company and c.isActive = true " +
            "order by c.priority desc")
    List<LoyaltyProgramConfig> findActiveConfigs(Company company);
}