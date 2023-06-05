package com.ddd.chulsi.infrastructure.config.datasource;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties("spring.datasource")
public class DatabaseProperty {

    private String url; // master url
//    private List<Slave> slaveList; // slave List

    private String username;
    private String password;
    private String driverClassName;

//    @Getter
//    @Setter
//    public static class Slave {
//        private String name; // slave
//        private String url;  // slave db url 주소
//    }

}

