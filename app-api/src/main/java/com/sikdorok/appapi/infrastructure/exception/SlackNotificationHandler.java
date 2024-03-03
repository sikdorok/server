package com.sikdorok.appapi.infrastructure.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlackNotificationHandler extends ServerException {

    public SlackNotificationHandler(String message) {
        super(400, message);
    }

}
