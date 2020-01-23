package com.adrian.rebollo.validator.validator;

import java.util.Collections;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.adrian.rebollo.validator.annotation.ValidCurrency;

public class CurrencyValidatorTest {

    @InjectMocks
    private CurrencyValidator currencyValidator;

    @Mock
    private ValidCurrency validCurrency;

    private ConstraintValidatorContextImpl constraintValidatorContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        currencyValidator.initialize(validCurrency);
        constraintValidatorContext = new ConstraintValidatorContextImpl(Collections.emptyList(), DefaultClockProvider.INSTANCE, PathImpl.createRootPath(), Mockito.mock(ConstraintDescriptor.class), new Object());
    }

    @Test
    public void assertInvalid() {
        Assert.assertFalse(currencyValidator.isValid("NaN", constraintValidatorContext));
    }

    @Test
    public void assertInvalidWhenNull() {
        Assert.assertFalse(currencyValidator.isValid(null, constraintValidatorContext));
    }

    public void assertInvalidWhenBlank() {
        Assert.assertFalse(currencyValidator.isValid("", constraintValidatorContext));
    }

    @Test
    public void assertValid() {
        Assert.assertTrue(currencyValidator.isValid("EUR", constraintValidatorContext));
    }
}
