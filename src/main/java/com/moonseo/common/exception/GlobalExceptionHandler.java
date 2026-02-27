package com.moonseo.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
        ErrorCode ec = ex.getErrorCode();

        Map<String, Object> details = ErrorDetails.baseDetails(request);
        details.putAll(ex.getExtraDetails());

        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ex.getMessage(), details));
    }

    // @Vaild 검증 실패 처리 (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.VALIDATION_ERROR;

        Map<String, Object> details = ErrorDetails.baseDetails(request);

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<FieldErrorDetail> errors = new ArrayList<>();

        for (FieldError fe : fieldErrors) {
            errors.add(new FieldErrorDetail(
                    fe.getField(),
                    fe.getRejectedValue(),
                    fe.getDefaultMessage()));
        }
        details.put("errors", errors);

        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getDefaultMessage(), details));
    }

    // 존재하지 않는 URL (404)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.NOT_FOUND;

        Map<String, Object> details = ErrorDetails.baseDetails(request);
        details.put("reason", "NO_HANDLER");

        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getDefaultMessage(), details));
    }

    // 메서드 오류(GET/POST 등)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.VALIDATION_ERROR;

        Map<String, Object> details = ErrorDetails.baseDetails(request);
        details.put("supportedMethods", ex.getSupportedMethods());

        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getDefaultMessage(), details));
    }

    // 예상 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        ErrorCode ec = ErrorCode.INTERNAL_ERROR;

        Map<String, Object> details = ErrorDetails.baseDetails(request);
        details.put("reason", "UNEXPECTED");

        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getDefaultMessage(), details));
    }
}
