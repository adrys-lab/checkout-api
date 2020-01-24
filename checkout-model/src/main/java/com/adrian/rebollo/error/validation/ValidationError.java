package com.adrian.rebollo.error.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* DTO to Wrap all Javax Error Validations
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError implements Serializable {

    private List<FieldError> errors = new ArrayList<>();

}
