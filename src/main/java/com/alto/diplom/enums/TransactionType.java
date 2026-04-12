package com.alto.diplom.enums;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum TransactionType implements EnumClass<Integer> {

    SALE(10),
    PAYBACK(20);

    private final Integer id;

    TransactionType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static TransactionType fromId(Integer id) {
        for (TransactionType at : TransactionType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}