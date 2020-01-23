package com.adrian.rebollo.validator.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.CollectionUtils;

import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.util.ErrorMessages;
import com.adrian.rebollo.validator.annotation.ValidNewOrder;

/**
 * Javax validator for ProductDtoValidator.
 */
public class NewOrderValidator extends AbstractValidator implements ConstraintValidator<ValidNewOrder, NewOrderDto> {

    private final CurrencyValidator currencyValidator;
    private final EmailValidator emailValidator;

    public NewOrderValidator() {
        this.currencyValidator = new CurrencyValidator();
        this.emailValidator = EmailValidator.getInstance();
    }

    public NewOrderValidator(CurrencyValidator currencyValidator, EmailValidator emailValidator) {
        this.currencyValidator = currencyValidator;
        this.emailValidator = emailValidator;
    }

    @Override
    public boolean isValid(final NewOrderDto newOrder, final ConstraintValidatorContext context) {
        if(CollectionUtils.isEmpty(newOrder.getProductList())) {
            addMessage(ErrorMessages.ORDER_PRODUCTS_EMPTY, context);
            return false;
        } else if(StringUtils.isBlank(newOrder.getEmail()) || !emailValidator.isValid(newOrder.getEmail())) {
            addMessage(ErrorMessages.ORDER_MAIL_EMPTY, context);
            return false;
        } else {
            return currencyValidator.isValid(newOrder.getCurrency(), context);
        }
    }
}
