package com.sikdorok.appapi.infrastructure.exception;

import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordNotMatchedException extends ServerException {

    public PasswordNotMatchedException() {
        super(400, ErrorMessage.PASSWORD_NOT_MATCHED);
    }

    public PasswordNotMatchedException(String message) {
        super(400, message);
    }

    public PasswordNotMatchedException(String message, String field) {
        super(400, message, field);
    }

}
