package com.adrian.rebollo.validator.validator;

import javax.money.Monetary;
import javax.money.UnknownCurrencyException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import com.adrian.rebollo.util.ErrorMessages;
import com.adrian.rebollo.validator.annotation.ValidCurrency;

/**
 * Javax validator for TimePeriod.
 */
public class CurrencyValidator extends AbstractValidator implements ConstraintValidator<ValidCurrency, String> {

    @Override
    public boolean isValid(final String currency, final ConstraintValidatorContext context) {
        if(StringUtils.isBlank(currency)) {
            addMessage(ErrorMessages.CURRENCY_BLANK, context);
            return false;
        } else {
            try {
                Monetary.getCurrency(currency);
                return true;
            } catch (UnknownCurrencyException e) {
                addMessage(ErrorMessages.CURRENCY_NOT_EXISTS, context);
                return false;
            }
        }
    }
}
