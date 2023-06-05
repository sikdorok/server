package com.ddd.chulsi.infrastructure.exception;

import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExistsException extends ServerException {

    public ExistsException() {
        super(409, ErrorMessage.DATA_EXISTS);
    }

    public ExistsException(String message) {
        super(409, message);
    }

    public ExistsException(String message, String field) {
        super(409, message, field);
    }

}
