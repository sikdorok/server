package com.ddd.chulsi.infrastructure.exception;

import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNotFoundException extends ServerException {

    public UserNotFoundException() {
        super(401, ErrorMessage.USER_NOT_FOUND);
    }

    public UserNotFoundException(String message) {
        super(401, message);
    }

    public UserNotFoundException(String message, String field) {
        super(401, message, field);
    }

}
