package com.sikdorok.appapi.infrastructure.config;

import com.sikdorok.appapi.infrastructure.filter.ReadableRequestWrapperFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class HttpRequestConfiguration {

    /**
     * ReadableRequestWrapperFilter 등록
     * 미등록시 POST Json 데이터 처리 불가능
     * @return
     */
    @Bean
    public FilterRegistrationBean<Filter> reReadableRequestFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>(new ReadableRequestWrapperFilter());
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/*"));
        return filterRegistrationBean;
    }

}
