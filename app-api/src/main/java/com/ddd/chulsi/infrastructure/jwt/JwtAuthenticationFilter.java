package com.ddd.chulsi.infrastructure.jwt;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import com.ddd.chulsi.infrastructure.exception.response.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null) {

            if (!authorization.startsWith(JwtTokenUtil.PREFIX)) {
                ErrorResponse.of(response, HttpStatus.UNAUTHORIZED, ErrorMessage.INVALID_JWT_TOKEN);
                return;
            }

            String token = authorization.replace(JwtTokenUtil.PREFIX, "");
            boolean isAccess = !request.getRequestURI().equals("/users/access-token");

            try {
                Authentication authentication = jwtTokenUtil.getAuthentication(token, properties, isAccess);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (TokenExpiredException e) {
                if (!request.getRequestURI().equals("/users/auto-login") && !request.getRequestURI().equals("/users/access-token")) {
                    ErrorResponse.of(response, HttpStatus.UNAUTHORIZED, ErrorMessage.TOKEN_EXPIRED_ERROR);
                    return;
                }
            } catch (Exception e) {
                log.error("================================================");
                log.error("JwtAuthenticationFilter - doFilterInternal() 오류발생");
                log.error("Exception Message : {}", e.getMessage());
                log.error("================================================");
                ErrorResponse.of(response, HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN);
                return;
            }

        }

        filterChain.doFilter(request, response);

    }

}