package com.alto.diplom.repository;

import com.alto.diplom.entity.core.CustomerGroup;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface CustomerGroupRepository extends JmixDataRepository<CustomerGroup, UUID> {
}