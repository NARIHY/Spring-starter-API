package com.base_spring_boot.com.security.model;
import java.util.Set;

public record OidcUser(String username, String email, String firstName,
                       String lastName, String birthday, String gender,
                       String phoneNumber, Set<String> roles) {
}