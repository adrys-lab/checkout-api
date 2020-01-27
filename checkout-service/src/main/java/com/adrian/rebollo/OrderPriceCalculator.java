package com.adrian.rebollo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import org.springframework.stereotype.Component;

import com.adrian.rebollo.dto.product.ExistingProductDto;

@Component
class OrderPriceCalculator {

    /**
     * convert all given product prices into order currency, getting the total amount in order currency.
     * @param products from which calculate the price based on its currencies
     * @param orderCurrency in which calculate the final order price.
     * @return the total amount.
     */
    BigDecimal calculatePriceInOrderCurrency(final List<ExistingProductDto> products, final String orderCurrency) {
        final CurrencyConversion toOrderCurrency = MonetaryConversions.getConversion(orderCurrency);
        final MonetaryAmountFactory<? extends MonetaryAmount > monetaryAmountFactory = Monetary.getDefaultAmountFactory();

        return products.stream()
                .map(product -> monetaryAmountFactory.setCurrency(product.getCurrency()).setNumber(product.getPrice()).create())
                .map(priceInProductCurrency -> priceInProductCurrency.with(toOrderCurrency))
                .map(priceInOrderCurrency -> BigDecimal.valueOf(priceInOrderCurrency.getNumber().doubleValue()).setScale(4, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
