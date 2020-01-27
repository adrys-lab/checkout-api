package com.adrian.rebollo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.port.OrderPort;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderPort orderPort;
    private final OrderPriceCalculator orderPriceCalculator;

    @Autowired
    public OrderServiceImpl(final OrderPort orderPort, final OrderPriceCalculator orderPriceCalculator) {
        this.orderPort = orderPort;
        this.orderPriceCalculator = orderPriceCalculator;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<OrderDto> create(final List<ExistingProductDto> products, final NewOrderDto newOrderDto) {

        final String orderCurrency = newOrderDto.getCurrency();
        final BigDecimal totalInOrderCurrency = orderPriceCalculator.calculatePriceInOrderCurrency(products, orderCurrency);

        final OrderDto orderDto = OrderDto.builder()
                .placedDate(LocalDateTime.now())
                .price(totalInOrderCurrency)
                .currency(orderCurrency)
                .email(newOrderDto.getEmail())
                .productList(products)
                .build();

        return orderPort.create(orderDto);
    }

    @Transactional
    public Page<OrderDto> get(final LocalDateTime date) {
        return orderPort.get(date);
    }

    /*
     Recover method when retries are exhausted for create a new order
     */
    @Recover
    public static Optional<ExistingProductDto> recoverCreate(final List<ExistingProductDto> products, final NewOrderDto newOrderDto) {
        LOGGER.error("Retries exhausted by ObjectOptimisticLockingFailureException when Creating Order {} with {}", newOrderDto, products);
        return Optional.empty();
    }
}
