package com.sikdorok.appapi.infrastructure.exception;

import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserExistsException extends ServerException {

    public UserExistsException() {
        super(409, ErrorMessage.USER_EXISTS);
    }

    public UserExistsException(String message) {
        super(409, message);
    }

    public UserExistsException(String message, String field) {
        super(409, message, field);
    }

}
