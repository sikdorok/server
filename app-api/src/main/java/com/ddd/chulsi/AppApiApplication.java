package com.ddd.chulsi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = RedisRepositoriesAutoConfiguration.class)
@EnableJpaAuditing // AuditingEntityListener 를 사용하기 위함
@ConfigurationPropertiesScan
public class AppApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApiApplication.class, args);
    }

}
