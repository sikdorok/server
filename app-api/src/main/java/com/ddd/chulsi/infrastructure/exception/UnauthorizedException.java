package com.ddd.chulsi.infrastructure.exception;

import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnauthorizedException extends ServerException {

    public UnauthorizedException() {
        super(401, ErrorMessage.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(401, message);
    }

    public UnauthorizedException(String message, String field) {
        super(401, message, field);
    }

}
