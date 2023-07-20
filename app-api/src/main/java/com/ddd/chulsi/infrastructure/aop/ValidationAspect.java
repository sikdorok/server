package com.ddd.chulsi.infrastructure.aop;

import com.ddd.chulsi.presentation.shared.request.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component // Bean 으로 사용
@Aspect // AOP 사용
@Slf4j
@RequiredArgsConstructor
public class ValidationAspect {

    @Pointcut("execution(* com.ddd.chulsi.*Controller.*(..))")
    public void validationPointcut(){ }

    @Before("validationPointcut()")
    public void before(JoinPoint joinPoint) {

        log.info("::: Start ValidationAspect ::: ");

        for (Object param : joinPoint.getArgs()) {
            if (param instanceof Validator) {
                ((Validator) param).verify();
            }
        }

        log.info("::: End ValidationAspect ::: ");

    }

}
