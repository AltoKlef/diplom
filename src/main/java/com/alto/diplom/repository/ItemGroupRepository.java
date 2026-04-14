package com.alto.diplom.repository;

import com.alto.diplom.entity.items.ItemGroup;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface ItemGroupRepository extends JmixDataRepository<ItemGroup, UUID> {
}