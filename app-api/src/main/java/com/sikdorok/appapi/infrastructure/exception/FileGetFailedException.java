package com.sikdorok.appapi.infrastructure.exception;

import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileGetFailedException extends ServerException {

    public FileGetFailedException() {
        super(400, ErrorMessage.FILE_GET_FAILED);
    }

    public FileGetFailedException(String message) {
        super(400, message);
    }

    public FileGetFailedException(String message, String field) {
        super(400, message, field);
    }

}

