package com.sikdorok.appapi.infrastructure.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthToken {
}
