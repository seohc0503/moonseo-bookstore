package com.moonseo.common.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public class ApiException extends RuntimeException{

    private final ErrorCode errorCode;
    private final Map<String, Object> extraDetails;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.extraDetails = Collections.emptyMap();
    }

    public ApiException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.extraDetails = Collections.emptyMap();
    }

    public ApiException(ErrorCode errorCode, Map<String, Object> extraDetails) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.extraDetails = extraDetails;
    }

    public ApiException(ErrorCode errorCode, String message, Map<String, Object> extraDetails) {
        super(message);
        this.errorCode = errorCode;
        this.extraDetails = (extraDetails != null) ? extraDetails : Collections.emptyMap();
    }
}
