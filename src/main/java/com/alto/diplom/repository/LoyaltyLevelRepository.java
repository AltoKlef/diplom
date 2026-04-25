package com.alto.diplom.repository;

import com.alto.diplom.entity.config.LoyaltyProgramConfig;
import com.alto.diplom.entity.core.Company;
import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import io.jmix.core.repository.JmixDataRepository;
import io.jmix.core.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoyaltyLevelRepository extends JmixDataRepository<LoyaltyLevel, UUID> {
    @Query("select l from LoyaltyLevel l " +
            "where l.loyaltyProgramConfig.company = :company " +
            "and l.loyaltyProgramConfig.isActive = true " +
            "and l.requiredSum = 0 " + // Уровень для новичков
            "order by l.loyaltyProgramConfig.priority desc")
    Optional<LoyaltyLevel> findDefaultLevel(Company company);

    @Query("select l from LoyaltyLevel l " +
            "where l.loyaltyProgramConfig = :config and l.requiredSum <= :spentSum " +
            "order by l.requiredSum desc")
    List<LoyaltyLevel> findApplicableLevels(LoyaltyProgramConfig config, BigDecimal spentSum);
}