package com.alto.diplom.entity;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum MarkStrategy implements EnumClass<String> {

    SPENDING("SPENDING"),
    EARNING("EARNING"),
    NONE("NONE");

    private final String id;

    MarkStrategy(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static MarkStrategy fromId(String id) {
        for (MarkStrategy at : MarkStrategy.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}