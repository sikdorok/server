package com.ddd.chulsi.infrastructure.exception.handler;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ddd.chulsi.infrastructure.exception.BadRequestException;
import com.ddd.chulsi.infrastructure.exception.ServerException;
import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import com.ddd.chulsi.infrastructure.exception.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String MESSAGE_KEY = "message : {}";
    private static final String FIELD_NAME_KEY = "{FieldName}";

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${slack.url}")
    private String slackUrl; // webhook url

    private final ObjectMapper objectMapper;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse noHandlerFoundException(NoHandlerFoundException exception) {
        log.error("message : {} : {}", ErrorMessage.NOT_FOUND_URL, exception.getRequestURL());
        return new ErrorResponse(404, ErrorMessage.NOT_FOUND_URL, exception.getRequestURL());
    }

    @ExceptionHandler(ServerException.class)
    public ErrorResponse handleServerException(ServerException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());
        return new ErrorResponse(exception.getCode(), exception.getMessage(), exception.getField());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ErrorResponse methodArgumentNotValidException(HttpMessageNotReadableException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        String message = ErrorMessage.BAD_REQUEST;
        String field = null;

        Throwable cause = exception.getCause();

        while (cause != null) {
            if (cause instanceof BadRequestException badRequestException) {
                message = badRequestException.getMessage();
                field = badRequestException.getField();
                break;
            } else if (cause instanceof InvalidFormatException) {
                message = "올바른 형식이 아닙니다.";
                break;
            }
            cause = cause.getCause();
        }

        return new ErrorResponse(400, message, field);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ErrorResponse missingServletRequestPartException(MissingServletRequestPartException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(400, ErrorMessage.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse illegalArgumentException(IllegalArgumentException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(400, ErrorMessage.BAD_REQUEST);
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ErrorResponse jwtDecodeException(JWTDecodeException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(400, ErrorMessage.INVALID_JWT_TOKEN);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ErrorResponse maxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(
            400,
            "한번에 요청 가능한 파일 사이즈는 20MB를 초과할 수 없습니다."
        );
    }

    /**
     * 요청 Method Exception
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponse httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(400, ErrorMessage.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorResponse httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(400, ErrorMessage.BAD_REQUEST_METHOD);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ErrorResponse tokenExpiredException(TokenExpiredException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(401, ErrorMessage.TOKEN_EXPIRED_ERROR);
    }

    /**
     * Validation 실패 (RequestBody)
     * HttpStatus 417
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        FieldError fieldError = exception.getBindingResult().getFieldError();
        String field = fieldError != null ? fieldError.getField() : null;

        return new ErrorResponse(
            417,
            ErrorMessage.EXPECTATION_FAILED_MSG.replace(FIELD_NAME_KEY, field),
            field
        );
    }

    /**
     * Validation 실패 (RequestBody)
     * HttpStatus 417
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ErrorResponse methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        return new ErrorResponse(
            417,
            ErrorMessage.EXPECTATION_FAILED_MSG.replace(FIELD_NAME_KEY, exception.getParameter().getParameterName()),
            exception.getParameter().getParameterName()
        );
    }

    /**
     * Validation 실패 (ModelAttribute)
     * HttpStatus 417
     */
    @ExceptionHandler(BindException.class)
    public ErrorResponse bindException(BindException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        String message = ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT;
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if (fieldError != null) {
            String field = fieldError.getField();
            message = ErrorMessage.EXPECTATION_FAILED_MSG.replace(FIELD_NAME_KEY, field);
        }

        return new ErrorResponse(417, message);
    }

    /**
     * Validation 실패 (RequestBody)
     * HttpStatus 417
     */
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ErrorResponse invalidDataAccessApiUsageException(InvalidDataAccessApiUsageException exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        String message = ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT;
        String field = null;
        if (StringUtils.isNotBlank(exception.getMessage())) {
            if (exception.getMessage().contains("Page index must not be less than zero!")) {
                field = "page";
            } else if (exception.getMessage().contains("Page size must not be less than one!")) {
                field = "size";
            }

            if (field != null) message = ErrorMessage.EXPECTATION_FAILED_MSG.replace(FIELD_NAME_KEY, field);
        }

        return new ErrorResponse(
          417,
            message
        );
    }

    /**
     * SQL Error
     */
    @ExceptionHandler({ DataIntegrityViolationException.class, ConstraintViolationException.class, SQLIntegrityConstraintViolationException.class })
    public ErrorResponse sqlException(Exception exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        int code = 500;
        String message = ErrorMessage.INTERNAL_SERVER_ERROR;

        Throwable throwable = exception.getCause();
        while(throwable != null) {
            if (throwable.getMessage().contains("Duplicate")) {
                code = 409;
                message = ErrorMessage.DATA_EXISTS;
            }
            throwable = throwable.getCause();
        }

        return new ErrorResponse(code, message);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleException(Exception exception) {
        log.error(MESSAGE_KEY, exception.getMessage());

        // slack noti
        slackSendMessage(exception);

        return new ErrorResponse(500, ErrorMessage.INTERNAL_SERVER_ERROR);
    }

    /**
     * Slack 500 Error Message Send
     * channel : chulsi-notification
     */
    private void slackSendMessage(Exception e) {
        RestTemplate restTemplate = new RestTemplate();

        // 현재 Request 받기
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Map<String, Object> restTemplateRequest = new HashMap<>();
        restTemplateRequest.put("username", "chulsi-500-noti"); //slack bot name

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("프로필 : ");
        stringBuilder.append(activeProfile);
        stringBuilder.append("\n");
        stringBuilder.append("```");
        stringBuilder.append("API : ");
        stringBuilder.append(request.getRequestURL());
        stringBuilder.append("```");
        stringBuilder.append("\n");
        stringBuilder.append("```");
        stringBuilder.append("내용 : ");
        stringBuilder.append(e.getMessage());
        stringBuilder.append("```");

        restTemplateRequest.put("text", stringBuilder); //전송할 메세지
        restTemplateRequest.put("icon_emoji", ":blob_fail:"); //slack bot image

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(restTemplateRequest);

        restTemplate.exchange(slackUrl, HttpMethod.POST, entity, String.class);
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
