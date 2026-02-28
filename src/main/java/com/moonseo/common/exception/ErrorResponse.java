package com.moonseo.common.exception;

import java.util.Map;

// 공통 응답
public record ErrorResponse(
        String code,
        String message,
        Map<String, Object> details
) {
    public ErrorResponse(ErrorCode errorCode, Map<String, Object> details) {
        this(errorCode.getCode(), errorCode.getDefaultMessage(), details);
    }
}
