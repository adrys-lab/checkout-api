package com.adrian.rebollo.error.validation;

import java.io.Serializable;

import lombok.Getter;

/**
* Field Error for Javax Error Validation Field
*/
@Getter
public class FieldError implements Serializable {

    private Integer code;
    private String message;

    public FieldError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}