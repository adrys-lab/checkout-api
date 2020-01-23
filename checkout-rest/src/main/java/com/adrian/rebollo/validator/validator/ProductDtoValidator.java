package com.adrian.rebollo.validator.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.util.ErrorMessages;
import com.adrian.rebollo.validator.annotation.ValidProductDto;

/**
 * Javax validator for ProductDtoValidator.
 */
public class ProductDtoValidator extends AbstractValidator implements ConstraintValidator<ValidProductDto, ProductDto> {

    private final CurrencyValidator currencyValidator;

    public ProductDtoValidator() {
        currencyValidator = new CurrencyValidator();
    }

    public ProductDtoValidator(CurrencyValidator currencyValidator) {
        this.currencyValidator = currencyValidator;
    }

    @Override
    public boolean isValid(final ProductDto productDto, final ConstraintValidatorContext context) {
        if(productDto == null) {
            addMessage(ErrorMessages.PRODUCT_NULL, context);
            return false;
        } else if(StringUtils.isBlank(productDto.getName())) {
            addMessage(ErrorMessages.PRODUCT_NAME_BLANK, context);
            return false;
        } else if(productDto.getPrice() < 0) {
            addMessage(ErrorMessages.PRODUCT_PRICE_MIN, context);
            return false;
        } else {
            return currencyValidator.isValid(productDto.getCurrency(), context);
        }
    }
}
