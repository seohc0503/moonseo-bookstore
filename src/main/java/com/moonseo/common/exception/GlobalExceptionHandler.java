package com.moonseo.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {

        ErrorCode ec = ErrorCode.INTERNAL_ERROR;

        String msg = (ex.getMessage() == null || ex.getMessage().isBlank()) ? ec.getDefaultMessage() : ex.getMessage();

        Map<String, Object> details = Map.of(
                "path", req.getRequestURI(),
                "method", req.getMethod(),
                "timestamp", Instant.now().toString(),
                "exception", ex.getClass().getSimpleName());

        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), msg, details));
    }
}
