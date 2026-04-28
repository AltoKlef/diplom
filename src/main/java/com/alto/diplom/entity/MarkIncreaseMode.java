package com.alto.diplom.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum MarkIncreaseMode implements EnumClass<String> {

    CAN_INCREASE("CAN_INCREASE"),
    CAN_NOT_INCREASE("CAN_NOT_INCREASE");

    private final String id;

    MarkIncreaseMode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static MarkIncreaseMode fromId(String id) {
        for (MarkIncreaseMode at : MarkIncreaseMode.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}