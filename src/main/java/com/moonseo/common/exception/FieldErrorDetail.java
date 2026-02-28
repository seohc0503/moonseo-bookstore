package com.moonseo.common.exception;

import org.springframework.validation.FieldError;

// Validation details용
public record FieldErrorDetail(
        String field,
        Object rejectedValue,
        String reason
) {
    public FieldErrorDetail(FieldError fe) {
        this(fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage());
    }
}
