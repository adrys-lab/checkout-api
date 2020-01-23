package com.adrian.rebollo.error.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
* DTO to Wrap all Javax Error Validations
*/
@Getter
public class ValidationError implements Serializable {

    private final List<FieldError> errors;

    public ValidationError(final List<FieldError> errors) {
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }
}
