package com.base_spring_boot.com.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


import java.util.Arrays;

@Configuration
public class AppConfig {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init() {
        // Accéder aux profils actifs
        String[] activeProfiles = environment.getActiveProfiles();
        System.out.println("Profils actifs : " + Arrays.toString(activeProfiles));
    }
}
