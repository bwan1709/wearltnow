package com.wearltnow.exception;

import lombok.Data;

@Data
public class AppException extends RuntimeException{
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    private final ErrorCode errorCode;
}
