package com.adrian.rebollo.platform.handler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.adrian.rebollo.error.validation.FieldError;
import com.adrian.rebollo.error.validation.ValidationError;

/**
 * Add a Global Validation Exception handler.
 * Handles Javax Validation messages and offer to the user in Error Info Wrapped class with validation message and correct HTTP Status.
 * Set it with highest precedence to put it up in the controller advise bean collections as it's exceptions is more concrete class
 * than GlobalExceptionHandler.
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ValidationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationError> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {

        final Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        final List<FieldError> errors = violations.stream()
                .peek(this::log)
                .map(violation -> new FieldError(HttpStatus.BAD_REQUEST.value(), violation.getMessage()))
                .collect(Collectors.toList());

        final ValidationError validationError = new ValidationError(errors);

        return new ResponseEntity<>(validationError, HttpStatus.BAD_REQUEST);
    }

    private void log(final ConstraintViolation<?> violation) {
        logger.info(String.format("Handled ConstraintViolationException and returning Validation Error Response for %s", violation.getMessage()));
    }
}
