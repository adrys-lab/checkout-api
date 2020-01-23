package com.adrian.rebollo.validator.validator;

import javax.validation.ConstraintValidatorContext;

import com.adrian.rebollo.util.ErrorMessages;

public class AbstractValidator {

    void addMessage(final ErrorMessages message, final ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message.getMessage()).addConstraintViolation();
    }
}
