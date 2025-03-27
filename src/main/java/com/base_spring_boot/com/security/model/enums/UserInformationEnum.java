package com.base_spring_boot.com.security.model.enums;

public enum UserInformationEnum {
    ADMIN("ADMIN_USER"),
    CLIENT("CLIENT_USER"),
    CONDUCTOR("CONDUCTOR_USER");
    private final String value;

    UserInformationEnum(String i) {
        this.value = i;
    }

    public String getValue() {
        return value;
    }
}
