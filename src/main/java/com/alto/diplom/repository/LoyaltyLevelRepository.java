package com.alto.diplom.repository;

import com.alto.diplom.entity.loyalty.LoyaltyLevel;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface LoyaltyLevelRepository extends JmixDataRepository<LoyaltyLevel, UUID> {
}