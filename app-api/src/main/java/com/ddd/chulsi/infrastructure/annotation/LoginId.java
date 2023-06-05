package com.ddd.chulsi.infrastructure.annotation;

import com.ddd.chulsi.infrastructure.util.StringUtil;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.*;
import java.util.Arrays;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginId.LoginIdValidator.class)
@Documented
public @interface LoginId {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 8;

    int max() default 11;

    String addMessageField() default "";

    boolean isRequired() default true;

    class LoginIdValidator implements ConstraintValidator<LoginId, String> {

        private int min;
        private int max;
        private String addMessageField;
        private boolean isRequired;

        @Override
        public void initialize(LoginId constraintAnnotation) {
            // 파라미터 초기화
            min = constraintAnnotation.min();
            max = constraintAnnotation.max();
            addMessageField = constraintAnnotation.addMessageField();
            isRequired = constraintAnnotation.isRequired();
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            // 필수입력 체크
            if (!isRequired) return true;

            // 값 유무체크
            if (StringUtils.isBlank(value)) {
                addConstraintViolation(context, String.format("%s필수 값입니다.", addMessageField));
                return false;
            }

            // 길이 검사
            if (value.length() < min || value.length() > max) {
                addConstraintViolation(context, String.format("%s%d자 ~ %d자 사이로 입력해주세요.", addMessageField, min, max));
                return false;
            }

            value = value.replace("-", "").replace(" ", "").trim();

            // 숫자 정규식 검사
            if (!StringUtil.isNumberValid(value)) return false;

            if (value.length() == 11) {
                String[] firstNumber = {"010", "011", "012", "013", "014", "015", "016", "017", "018", "019"};

                if (!Arrays.asList(firstNumber).contains(value.substring(0, 3))) {
                    addConstraintViolation(context, "휴대폰 형식이여야 합니다.");
                    return false;
                }
            }

            return true;
        }

        /**
         * 메시지 컨트롤
         */
        private void addConstraintViolation(ConstraintValidatorContext context, String message) {
            // 기본 메시지 비활성화
            context.disableDefaultConstraintViolation();
            // 새로운 메시지 추가
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }
    }
}
