package com.moonseo.common.exception;

// Validation details용
public record FieldErrorDetail(
        String field,
        Object rejectedValue,
        String reason
) {}
