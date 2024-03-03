package com.sikdorok.appapi.infrastructure.exception;

import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadRequestException extends ServerException {

    public BadRequestException() {
        super(400, ErrorMessage.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(400, message);
    }

    public BadRequestException(String message, String field) {
        super(400, message, field);
    }

}
