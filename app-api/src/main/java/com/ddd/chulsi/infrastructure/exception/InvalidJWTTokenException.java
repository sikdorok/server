package com.ddd.chulsi.infrastructure.exception;

import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidJWTTokenException extends ServerException {

    public InvalidJWTTokenException() {
        super(400, ErrorMessage.INVALID_JWT_TOKEN);
    }

    public InvalidJWTTokenException(String message) {
        super(400, message);
    }

    public InvalidJWTTokenException(String message, String field) {
        super(400, message, field);
    }

}
