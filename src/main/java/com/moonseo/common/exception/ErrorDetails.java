package com.moonseo.common.exception;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

// details 기본값 설정
public final class ErrorDetails {

    private ErrorDetails() {}

    public static Map<String, Object> baseDetails(HttpServletRequest request) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("requestId", UUID.randomUUID());
        details.put("path", request.getRequestURI());
        details.put("method", request.getMethod());
        details.put("timestamp", Instant.now().toString());
        return details;
    }
}
