package com.sikdorok.appapi.infrastructure.exception;

import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadException extends ServerException {

    public FileUploadException() {
        super(400, ErrorMessage.FILE_SAVE_FAILED);
    }

    public FileUploadException(String message) {
        super(400, message);
    }

    public FileUploadException(String message, String field) {
        super(400, message, field);
    }

}
