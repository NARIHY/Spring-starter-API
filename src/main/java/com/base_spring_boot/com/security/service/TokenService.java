package com.base_spring_boot.com.security.service;
import com.base_spring_boot.com.security.model.LoginAccessToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TokenService {
    @Value("${keycloak.auth-server-url}")
    private String kcUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final WebClient webClient;

    public TokenService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<LoginAccessToken> refreshToken(String refreshToken) {
        var map = new LinkedMultiValueMap<String, String>();
        map.put("client_secret", List.of(clientSecret));
        map.put("grant_type", List.of("refresh_token"));
        map.put("refresh_token", List.of(refreshToken));
        map.put("client_id", List.of(clientId));
        return webClient.post().uri(kcUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                .header("content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters
                        .fromFormData(new MultiValueMapAdapter<>(map))).retrieve().bodyToMono(LoginAccessToken.class);
    }
}
