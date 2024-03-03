package com.sikdorok.appapi.infrastructure.config.common.authUser;

import com.sikdorok.appapi.infrastructure.annotation.AuthToken;
import com.sikdorok.appapi.infrastructure.exception.InvalidJWTTokenException;
import com.sikdorok.appapi.infrastructure.exception.UnauthorizedException;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthTokenResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthToken.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {

        String authHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authHeader)) {
            String refreshToken = webRequest.getHeader(HttpHeaders.COOKIE);
            if (StringUtils.isBlank(refreshToken)) throw new UnauthorizedException();

            return refreshToken.replace("refreshToken=", "");
        }
        if (!authHeader.startsWith(JwtTokenUtil.PREFIX)) throw new InvalidJWTTokenException();

        // Bearer token
        return authHeader.split(" ")[1];

    }
}
