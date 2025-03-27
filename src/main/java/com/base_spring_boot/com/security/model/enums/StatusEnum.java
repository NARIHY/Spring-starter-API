package com.base_spring_boot.com.security.model.enums;

import lombok.Getter;

@Getter
public enum StatusEnum {
    VALID(1),
    INVALID(2),
    PENDING(3),
    CANCEL(4);

    private final Integer value;

    StatusEnum(Integer i) {
        this.value = i;
    }
    public Integer getValue() {
        return value;
    }
}
