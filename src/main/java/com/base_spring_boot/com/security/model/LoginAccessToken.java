package com.base_spring_boot.com.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginAccessToken(@JsonProperty("refresh_token") String refreshToken,
                               @JsonProperty("access_token") String accessToken) {
}