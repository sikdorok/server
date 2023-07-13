package com.ddd.chulsi.infrastructure.exception;

import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailSendFailedException extends ServerException {

    public EmailSendFailedException() {
        super(510, ErrorMessage.EMAIL_SEND_FAILED);
    }

    public EmailSendFailedException(String message) {
        super(510, message);
    }

    public EmailSendFailedException(String message, String field) {
        super(510, message, field);
    }

}

