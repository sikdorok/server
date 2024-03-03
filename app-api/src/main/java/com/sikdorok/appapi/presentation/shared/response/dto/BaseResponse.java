package com.sikdorok.appapi.presentation.shared.response.dto;

import com.sikdorok.appapi.infrastructure.exception.message.SuccessMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class BaseResponse<D> {

    private final int code;
    private final String message;
    private final D data;

    // success default response
    public static <D> BaseResponse<D> ofSuccess() {
        return new BaseResponse<>(200, SuccessMessage.SUCCESS_MSG, null);
    }

    // success response with data
    public static <D> BaseResponse<D> ofSuccess(D data) {
        return new BaseResponse<>(200, SuccessMessage.SUCCESS_MSG, data);
    }

    // success response with message and data
    public static <D> BaseResponse<D> ofSuccess(String message, D data) {
        return new BaseResponse<>(200, message, data);
    }

    // fail default response
    public static <D> BaseResponse<D> ofFail(String message) {
        return new BaseResponse<>(400, message, null);
    }

    // fail response with message and data
    public static <D> BaseResponse<D> ofFail(String message, D data) {
        return new BaseResponse<>(400, message, data);
    }

    // fail response with code and message
    public static <D> BaseResponse<D> ofFail(int code, String message) {
        return new BaseResponse<>(code, message, null);
    }

    // fail response with code and message and data
    public static <D> BaseResponse<D> ofFail(int code, String message, D data) {
        return new BaseResponse<>(code, message, data);
    }

}
