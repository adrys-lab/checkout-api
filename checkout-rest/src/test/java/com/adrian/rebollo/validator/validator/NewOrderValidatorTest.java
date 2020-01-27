package com.adrian.rebollo.validator.validator;

import java.util.Collections;
import java.util.UUID;
import javax.validation.metadata.ConstraintDescriptor;

import org.apache.commons.validator.routines.EmailValidator;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.adrian.rebollo.dto.order.NewOrderDto;

public class NewOrderValidatorTest {

    private static final String EMAIL = "adrian@rebollo.com";

    @InjectMocks
    private NewOrderValidator newOrderValidator;

    @Mock
    private CurrencyValidator currencyValidator;

    private ConstraintValidatorContextImpl constraintValidatorContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        newOrderValidator = new NewOrderValidator(currencyValidator, EmailValidator.getInstance());
        constraintValidatorContext = new ConstraintValidatorContextImpl(Collections.emptyList(), DefaultClockProvider.INSTANCE, PathImpl.createRootPath(), Mockito.mock(ConstraintDescriptor.class), new Object());
    }

    @Test
    public void assertInvalidWhenEmpty() {
        NewOrderDto newOrderDto = new NewOrderDto("EUR", EMAIL, Collections.emptyList());
        Assert.assertFalse(newOrderValidator.isValid(newOrderDto, constraintValidatorContext));
    }

    @Test
    public void initsWell() {
        NewOrderValidator validator = new NewOrderValidator();
        Assert.assertNotNull(ReflectionTestUtils.getField(validator, "currencyValidator"));
    }

    @Test
    public void assertInvalidWhenNull() {
        NewOrderDto newOrderDto = new NewOrderDto("EUR", EMAIL, null);
        Assert.assertFalse(newOrderValidator.isValid(newOrderDto, constraintValidatorContext));
    }

    @Test
    public void assertCallsCurrencyValidator() {
        final String currency = "NaN";
        NewOrderDto newOrderDto = new NewOrderDto(currency, EMAIL, Collections.singletonList(UUID.randomUUID()));

        newOrderValidator.isValid(newOrderDto, constraintValidatorContext);

        Mockito.verify(currencyValidator).isValid(currency, constraintValidatorContext);
    }

    @Test
    public void assertIsValid() {
        final String currency = "USD";
        NewOrderDto newOrderDto = new NewOrderDto(currency, EMAIL, Collections.singletonList(UUID.randomUUID()));

        Mockito.when(currencyValidator.isValid(currency, constraintValidatorContext)).thenReturn(true);

        Assert.assertTrue(newOrderValidator.isValid(newOrderDto, constraintValidatorContext));
    }
}
