package com.base_spring_boot.com.security.model.enums;

//Change
public enum RolesEnum {
    ADMIN("ROLE_ADMIN"),
    CLIENT("ROLE_CLIENT"),
    DRIVER("ROLE_DRIVER");

    private final String value;

    RolesEnum(String i) {
        this.value = i;
    }

    public String getValue() {
        return value;
    }
}
