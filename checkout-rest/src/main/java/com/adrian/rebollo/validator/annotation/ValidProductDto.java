package com.adrian.rebollo.validator.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.adrian.rebollo.validator.validator.ProductDtoValidator;

/*
 * Custom Annotation to inject a Time Period Validator in javax.validation context, triggered during Servlet Context API Resolving.
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Constraint(validatedBy = ProductDtoValidator.class )
public @interface ValidProductDto {
    String message() default "";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}