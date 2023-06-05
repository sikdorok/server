package com.ddd.chulsi.infrastructure.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JWTProperties {

    private String issuer;
    private String subject;
    private Long accessExpiresTime;
    private Long refreshExpiresTime;
    private String accessSecret;
    private String refreshSecret;

}
