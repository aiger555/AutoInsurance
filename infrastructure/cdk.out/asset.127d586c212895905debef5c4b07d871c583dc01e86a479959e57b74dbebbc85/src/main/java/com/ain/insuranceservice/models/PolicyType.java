package com.ain.insuranceservice.models;


public enum PolicyType {
    OSAGO("ОСАГО","Обязательное страхование автогражданской ответственности"),
    CASCO("КАСКО","Добровольное комплексное автострахование"),
    DSAGO("ДСАГО","Добровольное страхование автогражданской ответственности");

    private final String code;
    private final String description;

    PolicyType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
