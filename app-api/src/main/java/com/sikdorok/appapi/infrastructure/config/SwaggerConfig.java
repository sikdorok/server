//package com.sikdorok.appapi.infrastructure.config;
//
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.info.Info;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import io.swagger.v3.oas.models.servers.Server;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Collections;
//import java.util.List;
//
//@Configuration
//public class SwaggerConfig {
//
//    @Bean
//    public OpenAPI openAPI(){
//        SecurityScheme securityScheme = new SecurityScheme()
//            .type(SecurityScheme.Type.HTTP)
//            .scheme("Bearer")
//            .bearerFormat("JWT")
//            .in(SecurityScheme.In.HEADER)
//            .name("Authorization");
//
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList("JWT");
//
//        return new OpenAPI()
//            .servers(List.of(develop(), localhost()))
//            .components(new Components().addSecuritySchemes("JWT", securityScheme))
//            .security(Collections.singletonList(securityRequirement))
//            .info(
//                new Info()
//                    .title("Sikdorok")
//                    .description("식도록 API")
//                    .version("v1.0.0")
//            );
//    }
//
//    private Server localhost() {
//        return new Server()
//            .url("http://localhost:8080")
//            .description("Local Server");
//    }
//
//    private Server develop() {
//        return new Server()
//            .url("https://sikdorok.jeffrey-oh.click")
//            .description("Develop Server");
//    }
//
//}
