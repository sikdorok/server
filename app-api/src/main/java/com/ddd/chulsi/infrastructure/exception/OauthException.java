package com.ddd.chulsi.infrastructure.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthException extends ServerException {

    public OauthException(int code, String message) {
        super(code, message);
    }

}
