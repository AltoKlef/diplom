package com.alto.diplom.repository;

import com.alto.diplom.entity.TransactionConfig;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface TransactionConfigRepository extends JmixDataRepository<TransactionConfig, UUID> {
}