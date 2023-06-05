package com.ddd.chulsi.infrastructure.exception.response;

import com.ddd.chulsi.infrastructure.util.JsonUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

    private int code;
    private String message;
    private String field;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message) {
        return new ErrorResponse(httpStatus.value(), message);
    }

    public static ErrorResponse of(HttpStatus httpStatus, String message, String field) {
        return new ErrorResponse(httpStatus.value(), message, field);
    }

    public static void of(HttpServletResponse response, HttpStatus httpStatus, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.getWriter().write(JsonUtils.convertObjectToJson(ErrorResponse.of(httpStatus, message)));
    }

    public static void of(HttpServletResponse response, HttpStatus httpStatus, String message, String field) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.getWriter().write(JsonUtils.convertObjectToJson(ErrorResponse.of(httpStatus, message, field)));
    }

}
