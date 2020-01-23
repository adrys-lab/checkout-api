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

import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.validator.annotation.ValidProductDto;

public class ProductDtoValidatorTest {

    @InjectMocks
    private ProductDtoValidator productDtoValidator;

    @Mock
    private ValidProductDto validProductDto;
    @Mock
    private CurrencyValidator currencyValidator;

    private ConstraintValidatorContextImpl constraintValidatorContext;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        productDtoValidator.initialize(validProductDto);
        constraintValidatorContext = new ConstraintValidatorContextImpl(Collections.emptyList(), DefaultClockProvider.INSTANCE, PathImpl.createRootPath(), Mockito.mock(ConstraintDescriptor.class), new Object());
    }

    @Test
    public void assertInvalidWhenNull() {
        Assert.assertFalse(productDtoValidator.isValid(null, constraintValidatorContext));
    }

    @Test
    public void assertInvalidWhenNameNull() {
        final ProductDto productDto = new ProductDto(null, 0.0, "USD");
        Assert.assertFalse(productDtoValidator.isValid(productDto, constraintValidatorContext));
    }

    @Test
    public void assertInvalidWhenNameEmpty() {
        final ProductDto productDto = new ProductDto("", 0.0, "USD");
        Assert.assertFalse(productDtoValidator.isValid(productDto, constraintValidatorContext));
    }

    @Test
    public void assertInvalidWhenPriceLowerZero() {
        final ProductDto productDto = new ProductDto("ProductName", -10, "USD");
        Assert.assertFalse(productDtoValidator.isValid(productDto, constraintValidatorContext));
    }

    @Test
    public void assertInvalidWhenInvalidCurrency() {
        final String currency = "NaN";
        final ProductDto productDto = new ProductDto("ProductName", 10, currency);

        productDtoValidator.isValid(productDto, constraintValidatorContext);

        Mockito.verify(currencyValidator).isValid(currency, constraintValidatorContext);
    }

    @Test
    public void assertValid() {
        final String currency = "CHF";
        final ProductDto productDto = new ProductDto("ProductName", 10, currency);

        Mockito.when(currencyValidator.isValid(currency, constraintValidatorContext)).thenReturn(true);

        Assert.assertTrue(productDtoValidator.isValid(productDto, constraintValidatorContext));
    }
}
