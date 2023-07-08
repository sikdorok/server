package com.ddd.chulsi.infrastructure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.exception.InvalidJWTTokenException;
import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    public static final String PREFIX = "Bearer ";

    private static final String USER_ID_KEY = "userId";

    // JWT 토큰 인증정보 생성
    public String createToken(JWTClaim claim, JWTProperties properties, boolean isAccess) {
        Long expiresAt;
        String secret;

        if (isAccess) {
            expiresAt = properties.getAccessExpiresTime();
            secret = properties.getAccessSecret();
        } else {
            expiresAt = properties.getRefreshExpiresTime();
            secret = properties.getRefreshSecret();
        }

        return JWT.create()
            .withIssuer(properties.getIssuer())
            .withSubject(properties.getSubject())
            .withIssuedAt(new Date())
            .withExpiresAt(new Date(new Date().getTime() + expiresAt * 1000))
            .withClaim("auth", claim.getAuth().toString())
            .withClaim(USER_ID_KEY, claim.getUserId().toString())
            .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT decode(String token, JWTProperties properties) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getAccessSecret());

        JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer(properties.getIssuer())
            .build();

        try {
            return verifier.verify(token);
        } catch (SignatureVerificationException e) {
            throw new TokenExpiredException(ErrorMessage.TOKEN_EXPIRED_ERROR, null);
        }
    }

    public DecodedJWT decodeForRefreshToken(String token, JWTProperties properties) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getRefreshSecret());

        JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer(properties.getIssuer())
            .build();

        try {
            return verifier.verify(token);
        } catch (SignatureVerificationException e) {
            throw new TokenExpiredException(ErrorMessage.TOKEN_EXPIRED_ERROR, null);
        }
    }

    public JWTClaim getClaims(String token, JWTProperties properties, boolean isChecked) {
        DecodedJWT decodedJWT = decode(token, properties);

        return getJwtClaim(decodedJWT, isChecked);
    }

    public JWTClaim getClaimsForRefreshToken(String token, JWTProperties properties, boolean isChecked) {
        DecodedJWT decodedJWT = decodeForRefreshToken(token, properties);

        return getJwtClaim(decodedJWT, isChecked);
    }

    private JWTClaim getJwtClaim(DecodedJWT decodedJWT, boolean isChecked) {
        if (isChecked && decodedJWT.getExpiresAt().before(new Date()))
            throw new TokenExpiredException(ErrorMessage.TOKEN_EXPIRED_ERROR, null);

        Map<String, Claim> claims = decodedJWT.getClaims();

        Claim auth = claims.getOrDefault("auth", null);
        if (auth == null) throw new InvalidJWTTokenException(ErrorMessage.INVALID_JWT_TOKEN, "auth");

        Claim userId = claims.getOrDefault(USER_ID_KEY, null);
        if (userId == null) throw new InvalidJWTTokenException(ErrorMessage.INVALID_JWT_TOKEN, USER_ID_KEY);

        return JWTClaim.builder()
            .auth(DefinedCode.valueOf(auth.asString()))
            .userId(UUID.fromString(userId.asString()))
            .build();
    }

    // JWT 토큰 인증정보 조회
    public Authentication getAuthentication(String token, JWTProperties properties, boolean isAccess) {
        JWTClaim claims = isAccess ? getClaims(token, properties, true) : getClaimsForRefreshToken(token, properties, true);
        List<String> roles = new ArrayList<>();
        roles.add(claims.getAuth().toString());

        Collection<? extends GrantedAuthority> getAuthorities = roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(claims, null, getAuthorities);
    }

    // 인가 조회
    private JWTClaim checkNull(JWTClaim jwtClaim) {
        if (
            jwtClaim == null ||
            jwtClaim.getAuth() == null ||
            jwtClaim.getUserId() == null
        ) throw new InvalidJWTTokenException();

        return jwtClaim;
    }

    public JWTClaim checkAuth(String token, JWTProperties properties) {
        return checkNull(getClaims(token, properties, true));
    }

    public JWTClaim checkAuth(String token, JWTProperties properties, boolean isChecked) {
        return checkNull(getClaims(token, properties, isChecked));
    }

}