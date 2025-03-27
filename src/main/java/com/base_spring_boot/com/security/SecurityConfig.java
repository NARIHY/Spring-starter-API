package com.base_spring_boot.com.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe de configuration de la sécurité pour l'application.
 * Cette classe configure la sécurité des routes HTTP, l'authentification via JWT,
 * ainsi que la gestion des politiques CORS pour autoriser les requêtes provenant du frontend.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig   {

    @Value("${frontend.origin}")
    private String ihmUrl;

    public static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            final Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
            return ((List<String>)realmAccess.get("roles")).stream()
                    .map(roleName -> "ROLE_" + roleName) // prefix to map to a Spring Security "role"
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return jwtConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(c ->  corsConfigurationSource())
                .oauth2ResourceServer(o -> o.jwt(j -> j.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .csrf(CsrfConfigurer::disable)
                .authorizeHttpRequests(r  -> r.requestMatchers("/login").authenticated()
                        .anyRequest().permitAll()).oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/keycloak").successHandler(new CustomOauth2SuccesLoginHandler()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var corsSource = new CorsConfiguration();
        corsSource.setAllowedOriginPatterns(List.of(ihmUrl));
        corsSource.setAllowCredentials(true);
        corsSource.setAllowedMethods(List.of("POST", "PUT", "GET", "OPTIONS", "DELETE"));
        corsSource.setMaxAge(3600L);
        corsSource.setAllowedHeaders(List.of("authorization", "accept", "Content-Type"));
        corsSource.setExposedHeaders(List.of("authorization", "Content-Type", "x-total-count", "x-result-count", "x-user-allowed-methods", "content-disposition"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsSource);
        return source;
    }
    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();

    }
    @Bean
    public RequestRejectedHandler requestRejectedHandler() {
        return new HttpStatusRequestRejectedHandler();
    }
}
