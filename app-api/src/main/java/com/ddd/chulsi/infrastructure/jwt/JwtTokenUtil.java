package com.ddd.chulsi.infrastructure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.exception.BadRequestException;
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

    private static final String USERS_ID_KEY = "usersId";

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
            .withClaim(USERS_ID_KEY, claim.getUsersId().toString())
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

        Claim usersId = claims.getOrDefault(USERS_ID_KEY, null);
        if (usersId == null) throw new InvalidJWTTokenException(ErrorMessage.INVALID_JWT_TOKEN, USERS_ID_KEY);

        return JWTClaim.builder()
            .auth(DefinedCode.valueOf(auth.asString()))
            .usersId(UUID.fromString(usersId.asString()))
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
            jwtClaim.getUsersId() == null
        ) throw new InvalidJWTTokenException();

        return jwtClaim;
    }

    public JWTClaim checkAuth(String token, JWTProperties properties) {
        return checkNull(getClaims(token, properties, true));
    }

    public void checkAuthIsManager(String token, JWTProperties properties) {
        JWTClaim jwtClaim = checkNull(getClaims(token, properties, true));
        if (jwtClaim.getAuth() != DefinedCode.C000100001)
            throw new BadRequestException();
    }

    public Long getExpiration(String token) {
        DecodedJWT decode = JWT.decode(token);
        Long expiration = decode.getExpiresAt().getTime();
        Long now = new Date().getTime();
        return (expiration - now);
    }
}