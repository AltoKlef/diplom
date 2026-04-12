package com.alto.diplom.repository;

import com.alto.diplom.entity.transactions.TransactionItem;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface TransactionItemRepository extends JmixDataRepository<TransactionItem, UUID> {
}