package com.adrian.rebollo.validator.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.util.CollectionUtils;

import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.util.ErrorMessages;
import com.adrian.rebollo.validator.annotation.ValidNewOrder;

/**
 * Javax validator for ProductDtoValidator.
 */
public class NewOrderValidator extends AbstractValidator implements ConstraintValidator<ValidNewOrder, NewOrderDto> {

    private final CurrencyValidator currencyValidator;

    public NewOrderValidator() {
        this.currencyValidator = new CurrencyValidator();
    }

    public NewOrderValidator(CurrencyValidator currencyValidator) {
        this.currencyValidator = currencyValidator;
    }

    @Override
    public boolean isValid(final NewOrderDto newOrder, final ConstraintValidatorContext context) {
        if(CollectionUtils.isEmpty(newOrder.getProductList())) {
            addMessage(ErrorMessages.ORDER_PRODUCTS_EMPTY, context);
            return false;
        } else {
            return currencyValidator.isValid(newOrder.getCurrency(), context);
        }
    }
}
