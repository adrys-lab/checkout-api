package com.adrian.rebollo.validator.validator;

import java.time.LocalDateTime;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.adrian.rebollo.util.ErrorMessages;
import com.adrian.rebollo.validator.annotation.ValidTimePeriod;

/**
 * Javax validator for TimePeriod.
 */
public class TimePeriodValidator implements ConstraintValidator<ValidTimePeriod, LocalDateTime> {

    @Override
    public boolean isValid(final LocalDateTime timePeriod, final ConstraintValidatorContext context) {
        if(timePeriod == null || timePeriod.isAfter(LocalDateTime.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorMessages.ORDER_DATE_INVALID.getMessage()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
