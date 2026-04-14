package com.alto.diplom.repository;

import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import io.jmix.core.repository.JmixDataRepository;
import io.jmix.core.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface LoyaltyLevelRepository extends JmixDataRepository<LoyaltyLevel, UUID> {
    @Query("select l from LoyaltyLevel l " +
            "where l.loyaltyProgramConfig.company = :company " +
            "and l.loyaltyProgramConfig.isActive = true " +
            "and l.required_sum = 0 " + // Уровень для новичков
            "order by l.loyaltyProgramConfig.priority desc")
    Optional<LoyaltyLevel> findDefaultLevel(Company company);
}