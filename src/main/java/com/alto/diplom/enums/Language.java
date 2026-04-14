package com.alto.diplom.enums;

import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum Language implements EnumClass<String> {

    RU("RU"),
    EN("EN");

    private final String id;

    Language(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static Language fromId(String id) {
        for (Language at : Language.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}