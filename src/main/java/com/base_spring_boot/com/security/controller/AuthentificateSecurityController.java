package com.base_spring_boot.com.security.controller;

import com.base_spring_boot.com.security.service.OpenIdService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// Importez votre service OpenID selon son package

@Slf4j
@Controller
public class AuthentificateSecurityController {
    private static final Logger log = LoggerFactory.getLogger(AuthentificateSecurityController.class);

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${app.base.url}")
    private String appBaseUrl;

    @Value("${keycloak.client-id}")
    private String clientId;


    @Value("${frontend.login.url}")
    private String redirectUrl;

    @Value("${domain}")
    private String domain; // Actuellement vide dans votre properties

    private final OpenIdService openIDServices;

    public AuthentificateSecurityController(OpenIdService openIDServices) {
        this.openIDServices = openIDServices;
    }

    /**
     * Point de connexion.
     * Redirige vers l'URL de login définie par le service OpenID.
     */
    @GetMapping({"/login"})
    public void login(HttpServletResponse response, HttpSession session) throws IOException {
        openIDServices.loginRedirect(response, session);
    }

    /**
     * Point d'initiation de déconnexion.
     * Construit l'URL de logout de Keycloak en y ajoutant l'URI de redirection post‑déconnexion.
     */
    @GetMapping("/logout/init")
    public void logoutInit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postLogoutUri = URLEncoder.encode(appBaseUrl + "/logout/redirect", StandardCharsets.UTF_8);
        String logoutUrl = keycloakUrl
                + "/realms/" + keycloakRealm
                + "/protocol/openid-connect/logout?post_logout_redirect_uri="
                + postLogoutUri + "&client_id=" + clientId;
        response.sendRedirect(logoutUrl);
    }

    /**
     * Point de redirection post‑déconnexion.
     * Redirige vers l'URL de logout du frontend.
     */
    @GetMapping("/logout/redirect")
    public void logoutRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String finalRedirectUrl = redirectUrl + "/logout";
        response.addHeader("location", finalRedirectUrl);
        response.sendRedirect(finalRedirectUrl);
        log.info("Redirection vers : " + finalRedirectUrl);
    }
}
