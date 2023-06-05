package com.ddd.chulsi.infrastructure.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ReadableRequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("ReadableRequestWrapperFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest)request);
        chain.doFilter(wrapper, response);
    }

    @Override
    public void destroy() {
        log.info("ReadableRequestWrapperFilter destroy");
    }

}
