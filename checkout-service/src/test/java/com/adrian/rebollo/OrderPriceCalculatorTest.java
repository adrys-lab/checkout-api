package com.adrian.rebollo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adrian.rebollo.dto.product.ExistingProductDto;

@ExtendWith(MockitoExtension.class)
class OrderPriceCalculatorTest {

    private final ExistingProductDto prodUSD = new ExistingProductDto(UUID.randomUUID(), "", 5.0, "USD");
    private final ExistingProductDto prod2USD = new ExistingProductDto(UUID.randomUUID(), "", 15.0, "USD");

    private OrderPriceCalculator orderPriceCalculator;

    @BeforeEach
    void init() {
        orderPriceCalculator = new OrderPriceCalculator();
    }

    @Test
    void calculateTotalSameCurrency() {

        final String currency = "USD";
        final List<ExistingProductDto> products = Arrays.asList(prodUSD, prod2USD);

        final BigDecimal response = orderPriceCalculator.calculatePriceInOrderCurrency(products, currency);

        Assert.assertEquals(20.0, response.doubleValue(), 0);
    }
}
