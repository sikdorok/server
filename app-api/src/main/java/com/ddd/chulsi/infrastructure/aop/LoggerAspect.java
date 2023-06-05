package com.ddd.chulsi.infrastructure.aop;

import com.ddd.chulsi.infrastructure.aop.dto.LoggerDTO;
import com.ddd.chulsi.infrastructure.util.BCryptUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class LoggerAspect {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    private final ObjectMapper objectMapper;

    private LoggerDTO loggerDTO;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

    @Pointcut("execution(* com.ddd.chulsi..*Controller.*(..))")
    public void pointcut(){ }

    // TODO - 리팩토링 권장
    @AfterReturning(value = "pointcut()", returning = "returnObj")
    public void afterReturning(JoinPoint joinPoint, Object returnObj) throws IOException {
        log.info("::: Start DDD LoggerAspect AfterReturning ::: ");

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // health check logging pass
        if (request.getRequestURI().equals("/chulsi/") || request.getRequestURI().equals("/")) return;

        // GET pass
        if (request.getMethod().equals("GET")) return;

        String findWordInAnnotation = ", name";

        String controllerName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        StringBuilder remarkName = new StringBuilder();
        for (Annotation annotation : joinPoint.getTarget().getClass().getAnnotations()) {
            String str = annotation.toString();
            if (str.contains("RequestMapping") && str.contains(findWordInAnnotation)) {
                str = str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));
                String[] values = str.split("=");
                for (int i = 0; i < values.length; i++) {
                    String s = values[i];
                    if (s.contains(findWordInAnnotation)) {
                        String[] remarkArr = values[i + 1].split(",");
                        if (remarkArr.length > 2) {
                            for (int j = 0; j < remarkArr.length - 1; j++) remarkName.append(remarkArr[j]);
                        } else {
                            remarkName = new StringBuilder(remarkArr[0]);
                        }
                        break;
                    }
                }
            }
        }

        if (!remarkName.toString().equals("")) remarkName.append(" > ");

        String methodName = joinPoint.getSignature().getName();

        // Controller -> Method -> name 주석 내용 추출
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        boolean methodCheck = false;
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    String str = annotation.toString();
                    str = str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"));
                    String[] values = str.split("=");
                    for (int i = 0; i < values.length; i++) {
                        String s = values[i];
                        if (s.contains(findWordInAnnotation)) {
                            String[] remarkArr = values[i + 1].split(",");
                            if (remarkArr.length > 2) {
                                for (int j = 0; j < remarkArr.length - 1; j++) remarkName.append(remarkArr[j]);
                            } else {
                                remarkName.append(remarkArr[0]);
                            }
                            methodCheck = true;
                            break;
                        }
                    }

                    if (methodCheck) break;
                }
            }

            if (methodCheck) break;
        }

        // name 주석 없는 경우 빈 값 처리
        if (remarkName.toString().equals("\"\"")) remarkName = new StringBuilder();

        // Request Map
        Map<String, String[]> paramsMap = request.getParameterMap();
        Map<String, Object> paramsMapConvert = new HashMap<>();

        // convert
        if (paramsMap == null) return;
        for (Map.Entry<String, String[]> paramEntry : paramsMap.entrySet()) {
            String key = paramEntry.getKey();
            String[] values = paramEntry.getValue();

            paramsMapConvert.put(key, values.length == 1 ? values[0] : values);
        }

        // 비밀번호 암호화
        String passwordKey = "password";
        if (paramsMapConvert.containsKey(passwordKey)) paramsMapConvert.put(passwordKey, BCryptUtils.hash(String.valueOf(paramsMapConvert.get(passwordKey))));

        // Request Param jsonToString
        String paramsJson = objectMapper.writeValueAsString(paramsMapConvert);

        // Response jsonToString
        if (returnObj == null || returnObj.getClass().equals(ModelAndView.class)) return;
        String returnJson = objectMapper.writeValueAsString(returnObj);

        this.loggerDTO = LoggerDTO.builder()
            .uuid(UUID.randomUUID().toString())
            .controller(controllerName)
            .method(methodName)
            .message(remarkName.toString())
            .date(this.dateFormat.format(new Date()))
            .httpMethod(request.getMethod())
            .requestUri(request.getRequestURI())
            .requestBody(paramsJson)
            .responseBody(returnJson)
            .header(this.getHeaders(request))
            .build();

        log.info("log : {}", this.loggerDTO.toString()); // param에 담긴 정보들을 한번에 로깅한다.

        Map<String, String> loggerMap = new HashMap<>();
        loggerMap.put("requestUri", this.loggerDTO.getRequestUri());
        loggerMap.put("httpMethod", this.loggerDTO.getMethod());
        loggerMap.put("header", objectMapper.writeValueAsString(this.getHeaders(request)));
        loggerMap.put("requestBody", this.loggerDTO.getRequestBody());
        loggerMap.put("responseBody", this.loggerDTO.getResponseBody());
        loggerMap.put("date", this.loggerDTO.getDate());
        loggerMap.put("profileName", activeProfile);

        // System Log Save
        if (!this.loggerDTO.getMessage().equals("health check")) {
            // slack webhook 연결
        }
        
        log.info("::: End DDD LoggerAspect AfterReturning ::: ");
    }

    /**
     * 수행 실패 시
     * @param joinPoint - JoinPoint
     * @param exception - Exception
     * @throws RuntimeException
     */
    @AfterThrowing(value = "pointcut()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) throws RuntimeException, JsonProcessingException {
        log.info("::: Start DDD LoggerAspect AfterThrowing ::: ");



        // 현재 Request 받기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // exception 으로 해당 메서드에서 발생한 예외를 가져올 수 있다.
        log.error("exception : {}", exception.getLocalizedMessage());

        // Request Map
        Map<String, String[]> paramsMap = request.getParameterMap();
        Map<String, Object> paramsMapConvert = new HashMap<>();

        // convert
        for (Map.Entry<String, String[]> paramEntry : paramsMap.entrySet()) {
            String key = paramEntry.getKey();
            String[] values = paramEntry.getValue();

            paramsMapConvert.put(key, values.length == 1 ? values[0] : values);
        }

        // 비밀번호 암호화
        String passwordKey = "password";
        paramsMapConvert.computeIfPresent(passwordKey, (s, o) -> paramsMapConvert.put(s, BCryptUtils.hash(String.valueOf(paramsMapConvert.get(s)))));

        Map<String, String> loggerMap = new HashMap<>();
        loggerMap.put("requestUri", request.getRequestURI());
        loggerMap.put("httpMethod", request.getMethod());
        loggerMap.put("header", objectMapper.writeValueAsString(this.getHeaders(request)));
        loggerMap.put("requestBody", objectMapper.writeValueAsString(paramsMapConvert));
        loggerMap.put("responseBody", exception.toString());
        loggerMap.put("date", this.dateFormat.format(new Date()));
        loggerMap.put("profileName", activeProfile);
    
        // slack webhook

        log.info("::: End DDD LoggerAspect AfterThrowing ::: ");
    }

    /**
     * Header 추출
     *
     * @param request
     * @return Map<String, String>
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();

        String[] includeHeaders = {
            "host",
            "authorization",
            "refreshtoken",
            "user-agent"
        };

        Enumeration<String> headersNames = request.getHeaderNames();
        while (headersNames.hasMoreElements()) {
            String header = headersNames.nextElement();

            if (Arrays.stream(includeHeaders).anyMatch(header::contains)) {
                headers.put(header.toLowerCase(Locale.KOREA), request.getHeader(header));
            }
        }

        return headers;
    }

}
