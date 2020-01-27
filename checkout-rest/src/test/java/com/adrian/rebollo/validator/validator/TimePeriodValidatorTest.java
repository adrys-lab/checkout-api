package com.adrian.rebollo.validator.validator;

import java.time.LocalDateTime;
import java.util.Collections;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.DefaultClockProvider;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TimePeriodValidatorTest {

    @InjectMocks
    private TimePeriodValidator timePeriodValidator;

    private ConstraintValidatorContextImpl constraintValidatorContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        constraintValidatorContext = new ConstraintValidatorContextImpl(Collections.emptyList(), DefaultClockProvider.INSTANCE, PathImpl.createRootPath(), Mockito.mock(ConstraintDescriptor.class), new Object());
    }

    @Test
    public void assertInvalidWhenNull() {
        LocalDateTime timePeriod = null;
        Assert.assertFalse(timePeriodValidator.isValid(timePeriod, constraintValidatorContext));
    }

    @Test
    public void assertInvalidWhenAfter() {
        LocalDateTime timePeriod = LocalDateTime.of(2050, 10, 10, 10, 10);
        Assert.assertFalse(timePeriodValidator.isValid(timePeriod, constraintValidatorContext));
    }

    @Test
    public void assertValid() {
        LocalDateTime timePeriod = LocalDateTime.of(2019, 12, 30, 10, 10);
        Assert.assertTrue(timePeriodValidator.isValid(timePeriod, constraintValidatorContext));
    }
}
