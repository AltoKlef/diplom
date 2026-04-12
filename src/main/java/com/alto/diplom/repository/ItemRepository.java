package com.alto.diplom.repository;

import com.alto.diplom.entity.transactions.Item;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface ItemRepository extends JmixDataRepository<Item, UUID> {
}