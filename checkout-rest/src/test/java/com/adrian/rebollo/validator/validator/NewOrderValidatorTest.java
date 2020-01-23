package com.adrian.rebollo.validator.validator;

import java.util.Collections;
import java.util.UUID;
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

import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.validator.annotation.ValidNewOrder;

public class NewOrderValidatorTest {

    @InjectMocks
    private NewOrderValidator newOrderValidator;

    @Mock
    private ValidNewOrder validNewOrder;
    @Mock
    private CurrencyValidator currencyValidator;

    private ConstraintValidatorContextImpl constraintValidatorContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        newOrderValidator.initialize(validNewOrder);
        constraintValidatorContext = new ConstraintValidatorContextImpl(Collections.emptyList(), DefaultClockProvider.INSTANCE, PathImpl.createRootPath(), Mockito.mock(ConstraintDescriptor.class), new Object());
    }

    @Test
    public void assertInvalidWhenEmpty() {
        NewOrderDto newOrderDto = new NewOrderDto("EUR", Collections.emptyList());
        Assert.assertFalse(newOrderValidator.isValid(newOrderDto, constraintValidatorContext));
    }

    @Test
    public void assertInvalidWhenNull() {
        NewOrderDto newOrderDto = new NewOrderDto("EUR", null);
        Assert.assertFalse(newOrderValidator.isValid(newOrderDto, constraintValidatorContext));
    }

    @Test
    public void assertCallsCurrencyValidator() {
        final String currency = "NaN";
        NewOrderDto newOrderDto = new NewOrderDto(currency, Collections.singletonList(UUID.randomUUID()));

        newOrderValidator.isValid(newOrderDto, constraintValidatorContext);

        Mockito.verify(currencyValidator).isValid(currency, constraintValidatorContext);
    }

    @Test
    public void assertIsValid() {
        final String currency = "USD";
        NewOrderDto newOrderDto = new NewOrderDto(currency, Collections.singletonList(UUID.randomUUID()));

        Mockito.when(currencyValidator.isValid(currency, constraintValidatorContext)).thenReturn(true);

        Assert.assertTrue(newOrderValidator.isValid(newOrderDto, constraintValidatorContext));
    }
}
