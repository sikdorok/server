package com.sikdorok.appapi.infrastructure.config.common;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import com.sikdorok.appapi.infrastructure.exception.response.ErrorResponse;
import com.sikdorok.appapi.infrastructure.jwt.JWTProperties;
import com.sikdorok.appapi.infrastructure.jwt.JwtAuthenticationFilter;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.appapi.infrastructure.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String[] MANAGER_WHITELIST = {
        "/admin/**"
    };

    private static final String[] PERMIT_LIST = {
        "/users/**"
    };

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;

    private final RedisUtil redisUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .httpBasic(AbstractHttpConfigurer::disable) // rest api 만을 고려하여 기본 설정은 해제
            .csrf(AbstractHttpConfigurer::disable) // csrf 보안 토큰 disable 처리
            .cors(Customizer.withDefaults())
            .exceptionHandling(handling -> handling
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 토큰 기반 인증이므로 세션 미사용
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PERMIT_LIST).permitAll()
                .requestMatchers(HttpMethod.POST, "/app-version").hasAuthority(String.valueOf(DefinedCode.C000100001))
                .requestMatchers(MANAGER_WHITELIST).hasAuthority(String.valueOf(DefinedCode.C000100001))
                .anyRequest().permitAll() // 그외 나머지 요청은 누구나 접근 가능
            ) // 요청에 대한 사용권한 체크
            .oauth2Login()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil, properties, redisUtil), UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    // 올바르지 않은 URL 요청이여도 filter 처리가 우선으로 진행되는 중
    // auto-login1 -> jwtFilter 우선 타느라 401이 뜸
    private final AccessDeniedHandler accessDeniedHandler = (request, response, accessDeniedException) -> ErrorResponse.of(response, HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN);

    private final AuthenticationEntryPoint authenticationEntryPoint = (request, response, authException) -> ErrorResponse.of(response, HttpStatus.FORBIDDEN, ErrorMessage.FORBIDDEN);


}
