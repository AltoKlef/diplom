package com.alto.diplom.repository;

import com.alto.diplom.entity.transactions.Transaction;
import io.jmix.core.repository.JmixDataRepository;

import java.util.UUID;

public interface TransactionRepository extends JmixDataRepository<Transaction, UUID> {
}