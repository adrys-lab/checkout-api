package com.adrian.rebollo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.money.Monetary;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.port.OrderPort;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderPort orderPort;

    @Autowired
    public OrderServiceImpl(final OrderPort orderPort) {
        this.orderPort = orderPort;
    }

    @Transactional
    public Optional<OrderDto> create(final List<ExistingProductDto> products, final String currency) {

        final BigDecimal totalInOrderCurrency = getPriceInOrderCurrency(products, currency);

        final OrderDto orderDto = OrderDto.builder()
                .placedDate(LocalDateTime.now())
                .price(totalInOrderCurrency)
                .currency(currency)
                .productList(products.stream().map(productDetail -> new ExistingProductDto(productDetail.getId(), productDetail.getName(), productDetail.getPrice(), productDetail.getCurrency())).collect(Collectors.toList()))
                .build();

        return orderPort.create(orderDto);
    }

    /**
     * get all prices of all products, in the product currencies, and convert them into order currency, calculating total amount.
     * @param products from which calculate the price based on its currencies
     * @param currency in which calculate the final order price.
     * @return the total amount.
     */
    private BigDecimal getPriceInOrderCurrency(List<ExistingProductDto> products, String currency) {
        final CurrencyConversion priceToOrderCurrency = MonetaryConversions.getConversion(currency);

        return products.stream()
                .map(product -> Monetary.getDefaultAmountFactory().setCurrency(product.getCurrency()).setNumber(product.getPrice()).create())
                .map(priceInProductCurrency -> priceInProductCurrency.with(priceToOrderCurrency))
                .map(priceInOrderCurrency -> BigDecimal.valueOf(priceInOrderCurrency.getNumber().doubleValue()).setScale(4, RoundingMode.HALF_UP))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Page<OrderDto> get(final LocalDateTime date) {
        return orderPort.get(date);
    }
}
