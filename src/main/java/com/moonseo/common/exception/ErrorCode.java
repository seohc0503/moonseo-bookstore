package com.moonseo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "요청 값이 올바르지 않습니다."),
    AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "대상을 찾을 수 없습니다."),
    CONFLICT(HttpStatus.CONFLICT, "CONFLICT", "요청이 현재 상태와 충돌합니다."),
    STATE_INVALID(HttpStatus.CONFLICT, "STATE_INVALID", "상태 전이가 불가능합니다."),
    PAYMENT_ERROR(HttpStatus.BAD_GATEWAY, "PAYMENT_ERROR", "결제 연동 오류입니다."),
    IDEMPOTENCY_CONFLICT(HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT", "멱등성 충돌입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류입니다.");

    public ApiException exception() {
        return new ApiException(this);
    }
    public ApiException exception(String message) {
        return new ApiException(this, message);
    }
    public ApiException exception(String message, Map<String, Object> extraDetails) {
        return new ApiException(this, message, extraDetails);
    }

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;
}
