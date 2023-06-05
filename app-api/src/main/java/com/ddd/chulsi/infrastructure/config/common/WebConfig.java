package com.ddd.chulsi.infrastructure.config.common;

import com.ddd.chulsi.infrastructure.config.common.authUser.AuthTokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig extends WebMvcConfigurationSupport {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final AuthTokenResolver authTokenResolver;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(authTokenResolver);
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        if (activeProfile.equals("prod")) {
            registry
                .addMapping("/**")
                .exposedHeaders("Content-Disposition", "Set-Cookie")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTION")
                .allowedOrigins("https://chulsi.ddd");
        } else {
            registry
                .addMapping("/**")
                .exposedHeaders("Content-Disposition", "Set-Cookie")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTION")
                .allowedOriginPatterns("*");
        }
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
            .addResourceLocations(
                "classpath:/META-INF/resources/",
                "classpath:/resources/",
                "classpath:/static/",
                "classpath:/public/"
            );
    }

}
