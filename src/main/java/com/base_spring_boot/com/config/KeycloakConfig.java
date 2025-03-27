package com.base_spring_boot.com.config;

import jakarta.ws.rs.client.Client;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${custom.keycloak.service-account.credentials.secret}")
    private String clientSecret;

    @Value("${custom.keycloak.service-account.resource}")
    private String clientId;

    @Value("${keycloak.auth-server-url}")
    private String authUrl;

    @Value("${keycloak.realm}")
    private String realm;

    // RestEasy client to interact with Keycloak's REST API
    @Bean
    public Client getRestEasyClient() {
        return ResteasyClientBuilder.newBuilder().build();
    }

    // Keycloak instance configured for client credentials flow
    @Bean
    public Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .serverUrl(authUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .resteasyClient(getRestEasyClient())
                .build();
    }
}
