package com.alto.diplom.core.config;

import jakarta.persistence.criteria.CriteriaBuilder;

import java.math.BigDecimal;

public interface CompanyDefaults {
    String DEFAULT_CATALOG_NAME = "Основной каталог";
    String DEFAULT_LOYALTY_NAME = "Программа для всех";
    BigDecimal DEFAULT_CASHBACK = BigDecimal.ONE;
    BigDecimal DEFAULT_MARK_SPEND = BigDecimal.ONE;
    Integer DEFAULT_PRIORITY = 100;
    Short DEFAULT_LEVEL_NUMBER=1;
}