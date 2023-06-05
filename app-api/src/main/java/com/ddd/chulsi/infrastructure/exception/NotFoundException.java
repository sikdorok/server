package com.ddd.chulsi.infrastructure.exception;

import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends ServerException {

    public NotFoundException() {
        super(404, ErrorMessage.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(404, message);
    }

    public NotFoundException(String message, String field) {
        super(404, message, field);
    }

}
