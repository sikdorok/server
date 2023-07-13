package com.ddd.chulsi.infrastructure.exception.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessage {

    public static final String TOKEN_EXPIRED_ERROR = "만료된 토큰입니다.";

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";

    public static final String INVALID_JWT_TOKEN = "토큰이 올바르지 않습니다.";

    public static final String PASSWORD_NOT_MATCHED = "비밀번호가 일치하지 않습니다.";

    public static final String USER_EXISTS = "이미 존재하는 유저입니다.";

    public static final String USER_NOT_FOUND = "존재하지 않는 유저입니다.";

    public static final String UNAUTHORIZED = "인증 정보가 잘못되었습니다.";

    public static final String NOT_FOUND = "존재하지 않는 데이터입니다.";

    public static final String DATA_EXISTS = "이미 존재하는 데이터입니다.";

    public static final String EXPECTATION_FAILED_MSG_DEFAULT = "값이 올바르지 않습니다.";

    public static final String EXPECTATION_FAILED_MSG = "{FieldName}, 값이 올바르지 않습니다.";

    public static final String BAD_REQUEST = "잘못된 요청입니다.";

    public static final String BAD_REQUEST_METHOD = "잘못된 메소드 요청입니다.";

    public static final String FORBIDDEN = "접근 권한이 없습니다.";

    public static final String NOT_FOUND_URL = "존재하지 않는 URL입니다.";

    public static final String FILE_SAVE_FAILED = "파일 저장에 실패했습니다.";

    public static final String FILE_DELETE_FAILED = "파일 삭제에 실패했습니다.";

    public static final String OAUTH_RESPONSE_EMPTY = "{oauth} Oauth 결과가 없습니다.";

    public static final String OAUTH_REQUEST_FAILED = "{oauth} Oauth 요청에 실패했습니다.";

    public static final String EMAIL_SEND_FAILED = "이메일 발송에 실패했습니다.";
}
