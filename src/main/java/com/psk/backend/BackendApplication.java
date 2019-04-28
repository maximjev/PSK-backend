package com.psk.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    private static final String SPRING_ACTIVE_PROFILES = "spring.profiles.active";
    private static final String PROD_PROFILE = "prod";

    public static void main(String[] args) {
        if (System.getProperty(SPRING_ACTIVE_PROFILES) == null) {
            System.setProperty(SPRING_ACTIVE_PROFILES, PROD_PROFILE);
        }

        SpringApplication.run(BackendApplication.class, args);
    }
}
