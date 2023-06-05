package com.ddd.chulsi.infrastructure.exception;

import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDeleteFailedException extends ServerException {

    public FileDeleteFailedException() {
        super(400, ErrorMessage.FILE_DELETE_FAILED);
    }

    public FileDeleteFailedException(String message) {
        super(400, message);
    }

    public FileDeleteFailedException(String message, String field) {
        super(400, message, field);
    }

}

