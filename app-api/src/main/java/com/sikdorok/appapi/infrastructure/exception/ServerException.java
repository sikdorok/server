package com.sikdorok.appapi.infrastructure.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerException extends RuntimeException {

    private final int code;
    private final String message;
    private final String field;

    public ServerException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.field = null;
    }

    public ServerException(int code, String message, String field) {
        super(message);
        this.code = code;
        this.message = message;
        this.field = field;
    }

}
