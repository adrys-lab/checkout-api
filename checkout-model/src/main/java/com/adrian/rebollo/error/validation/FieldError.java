package com.adrian.rebollo.error.validation;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* Field Error for Javax Error Validation Field
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldError implements Serializable {

    private Integer code;
    private String message;
}