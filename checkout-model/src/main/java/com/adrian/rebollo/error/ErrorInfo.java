package com.adrian.rebollo.error;

import java.io.Serializable;

import lombok.Getter;

/*
* Class to wrap internal checked exceptions for the end-user
*/
@Getter
public class ErrorInfo implements Serializable {

    private static final long serialVersionUID = -92651231312091234L;

    private final String message;
    private final String detail;
    private final Integer code;

    public ErrorInfo(final String message, final String detail, final Integer code) {
        this.message = message;
        this.detail = detail;
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
