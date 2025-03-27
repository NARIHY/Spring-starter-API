package com.base_spring_boot.com.security.service;

import com.base_spring_boot.com.security.model.OidcUser;
import org.keycloak.representations.idm.UserRepresentation;
import com.base_spring_boot.com.security.model.enums.RolesEnum;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour gérer l'authentification OpenID (via Keycloak) et la gestion des utilisateurs
 * incluant des opérations comme la redirection de connexion/déconnexion et l'extraction des rôles.
 */
@Service
public class OpenIdService {

    // Constantes pour les noms des cookies utilisés pour stocker les tokens
    public static final String OIDC_TOKEN = "oidc_token";
    public static final String OIDC_REFRESH_TOKEN = "oidc_refresh_token";

    @Value("${frontend.login.url}")
    String redirectUrl;  // URL de redirection après la connexion ou la déconnexion

    @Value("${domain}")
    String domain;  // Domaine pour le cookie de token

    @Value("${keycloak.realm}")
    private String realm;  // Nom du domaine de Keycloak

    @Value("${keycloak.client-id}")
    private String ressourceId;

    @Value("${app.deployement.status}")
    private String appDeployementStatus;

    private final Keycloak keycloak;  // Client Keycloak pour l'interaction avec le serveur d'authentification
    private final OAuth2AuthorizedClientService clientService;  // Service pour accéder aux clients OAuth2 autorisés

    /**
     * Constructeur pour injecter les dépendances nécessaires.
     */
    public OpenIdService(Keycloak keycloak, OAuth2AuthorizedClientService clientService) {
        this.keycloak = keycloak;
        this.clientService = clientService;
    }

    /**
     * Effectue la redirection après une connexion réussie en créant et en envoyant des cookies pour le token et le refresh token.
     * Ensuite, invalide la session HTTP pour forcer un nouveau login si nécessaire.
     */
    public void loginRedirect(HttpServletResponse response, HttpSession httpSession) throws IOException {
        var principal = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        var accessToken = clientService.loadAuthorizedClient(principal.getAuthorizedClientRegistrationId(), principal.getName()).getAccessToken().getTokenValue();
        var refreshToken = clientService.loadAuthorizedClient(principal.getAuthorizedClientRegistrationId(), principal.getName()).getRefreshToken().getTokenValue();

        // Création des cookies
        var accessTokenCookie = new Cookie(OIDC_TOKEN, accessToken);
        var refreshTokenCookie = new Cookie(OIDC_REFRESH_TOKEN, refreshToken);

        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(86400 * 60);  // 60 jours

        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(86400 * 60);  // 60 jours

        if (domain != null && !domain.isEmpty()) {
            accessTokenCookie.setDomain(domain);
            refreshTokenCookie.setDomain(domain);
        }

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        response.sendRedirect(redirectUrl + "/login");

        httpSession.invalidate();
    }


    /**
     * Gère la déconnexion de l'utilisateur en redirigeant vers la page appropriée.
     * La logique de déconnexion complète est encore à implémenter.
     */
    public void logoutRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // À compléter selon les besoins de gestion de la déconnexion
    }

    //getOidc user
    @PreAuthorize("this.isConnected()")
    public OidcUser getUserInfo() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            return new OidcUser(jwt.getClaim("preferred_username"), jwt.getClaim("email"),
                    jwt.getClaim("given_name"), jwt.getClaim("family_name"),
                    null, null, null,
                    getCurrentUserRoles().stream().map(g -> g.getAuthority().replaceAll("ROLE_", ""))
                            .collect(Collectors.toSet()));
        }
        return null;
    }



    /**
     * Récupère le nom d'utilisateur actuel.
     */
    public String getCurrentUserName() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt.getClaim("preferred_username");
        }
        return null;
    }

    /**
     * Vérifie si un utilisateur est connecté en vérifiant si le principal est un JWT.
     */
    public boolean isConnected() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof Jwt;  // Retourne vrai si l'utilisateur est connecté via OpenID
    }

    /**
     * Récupère la liste des rôles de l'utilisateur actuel.
     */
//    public List<? extends GrantedAuthority> getCurrentUserRoles() {
//        var auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
//        return auth.stream().toList();
//    }
    public  Collection<? extends GrantedAuthority> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) auth).getToken();

            // Extraire "resource_access" du token
            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

            if (resourceAccess != null && resourceAccess.containsKey(ressourceId)) { // Remplace "test-app" par ton client-id
                Object testAppRoles = resourceAccess.get(ressourceId);

                if (testAppRoles instanceof Map) {
                    Map<String, Object> rolesMap = (Map<String, Object>) testAppRoles;
                    Object rolesObject = rolesMap.get("roles");

                    if (rolesObject instanceof Collection) {
                        Collection<String> roles = (Collection<String>) rolesObject;

                        return roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())) // Ajout "ROLE_" pour cohérence avec Spring Security
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Vérifie si l'utilisateur actuel a le rôle ADMIN.
     */
    public boolean isAdmin() {
        return getCurrentUserRoles().stream().anyMatch(authority -> Objects.equals(authority.getAuthority(), RolesEnum.ADMIN.getValue()));
    }

    /**
     * Vérifie si l'utilisateur actuel a le rôle DRIVER.
     */
    public boolean isDriver() {
        return getCurrentUserRoles().stream().anyMatch(authority -> Objects.equals(authority.getAuthority(), RolesEnum.DRIVER.getValue()));
    }

    /**
     * Vérifie si l'utilisateur actuel a le rôle CLIENT.
     */
    public boolean isClient() {
        return getCurrentUserRoles().stream().anyMatch(authority -> Objects.equals(authority.getAuthority(), RolesEnum.CLIENT.getValue()));
    }

}
