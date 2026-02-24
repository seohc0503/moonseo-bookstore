package com.moonseo.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(String code, String message, Map<String, Object> details) {}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
        ErrorCode ec = ErrorCode.INTERNAL_ERROR;

        Map<String, Object> details = Map.of(
                "path", req.getRequestURI(),
                "method", req.getMethod(),
                "timestamp", Instant.now().toString(),
                "exception", ex.getClass().getSimpleName());

        String msg = (ex.getMessage() == null || ex.getMessage().isBlank()) ? ec.getDefaultMessage() : ex.getMessage();

        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), msg, details));
    }
}
