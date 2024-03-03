package com.sikdorok.appapi.infrastructure.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JWTConfig {

    @Bean
    @ConfigurationProperties(prefix = "jwt")
    public JWTProperties jwtProperties() {
        return new JWTProperties();
    }

}
