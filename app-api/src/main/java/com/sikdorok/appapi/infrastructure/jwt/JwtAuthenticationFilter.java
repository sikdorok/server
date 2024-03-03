package com.sikdorok.appapi.infrastructure.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import com.sikdorok.appapi.infrastructure.exception.response.ErrorResponse;
import com.sikdorok.appapi.infrastructure.util.RedisUtil;
import io.lettuce.core.RedisConnectionException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;

    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null) {

            if (!authorization.startsWith(JwtTokenUtil.PREFIX)) {
                ErrorResponse.of(response, HttpStatus.UNAUTHORIZED, ErrorMessage.INVALID_JWT_TOKEN);
                return;
            }

            if (!request.getRequestURI().contains("oauth")) {
                String token = authorization.replace(JwtTokenUtil.PREFIX, "");
                boolean isAccess = !request.getRequestURI().equals("/users/access-token");

                try {
                    // Redis에 해당 accessToken이 "logout" 으로 블랙리스트에 올라가있는지 확인
                    CompletableFuture<Object> isLogout = redisUtil.get(token);

                    if(ObjectUtils.isEmpty(isLogout.get())) {
                        Authentication authentication = jwtTokenUtil.getAuthentication(token, properties, isAccess);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else if(isLogout.get().equals("logout")) {
                        ErrorResponse.of(response, HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN);
                        return;
                    }
                } catch (TokenExpiredException e) {
                    if (!request.getRequestURI().equals("/users/auto-login") && !request.getRequestURI().equals("/users/access-token")) {
                        ErrorResponse.of(response, HttpStatus.UNAUTHORIZED, ErrorMessage.TOKEN_EXPIRED_ERROR);
                        return;
                    }
                } catch (ExecutionException | RedisConnectionFailureException | RedisConnectionException ignored) {

                } catch (Exception e) {
                    log.error("================================================");
                    log.error("JwtAuthenticationFilter - doFilterInternal() 오류발생");
                    log.error("Exception Message : {}", e.getMessage());
                    log.error("================================================");
                    ErrorResponse.of(response, HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN);
                    return;
                }
            }

        }

        filterChain.doFilter(request, response);

    }

}