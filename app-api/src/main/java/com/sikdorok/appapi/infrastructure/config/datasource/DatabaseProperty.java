package com.sikdorok.appapi.infrastructure.config.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("spring.datasource")
public class DatabaseProperty {

    private String url;
    private String username;
    private String password;
    private String driverClassName;


}

