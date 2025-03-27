package com.base_spring_boot.com.security.controller;

import com.base_spring_boot.com.security.model.LoginAccessToken;
import com.base_spring_boot.com.security.service.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.RequestBody;


import java.io.IOException;

@RestController
public class SecurityRestController {
    private final TokenService tokenServices;

    public SecurityRestController(TokenService tokenServices) {
        this.tokenServices = tokenServices;
    }

    @PostMapping("/refresh")
    public Mono<LoginAccessToken> refreshToken(@RequestBody LoginAccessToken refreshToken) throws IOException {
        return tokenServices.refreshToken(refreshToken.refreshToken());
    }
}
