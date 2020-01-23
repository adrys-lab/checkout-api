package com.adrian.rebollo.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adrian.rebollo.OrderService;
import com.adrian.rebollo.OrderServiceImpl;
import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.port.OrderPort;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private final ExistingProductDto prodUSD = new ExistingProductDto(UUID.randomUUID(), "", 5.0, "USD");
    private final ExistingProductDto prod2USD = new ExistingProductDto(UUID.randomUUID(), "", 5.0, "USD");

    private OrderService orderService;

    @Mock
    private OrderPort orderPort;

    @Captor
    private ArgumentCaptor<OrderDto> orderCaptor;

    @BeforeEach
    public void init() {
        orderService = new OrderServiceImpl(orderPort);
    }

    @Test
    public void calculateTotalTest() {

        final String currency = "USD";
        final String email = "adrian@rebollo.com";
        NewOrderDto newOrderDto = new NewOrderDto(currency, email, Collections.emptyList());
        final List<ExistingProductDto> products = Arrays.asList(prodUSD, prod2USD);

        orderService.create(products, newOrderDto);

        Mockito.verify(orderPort).create(orderCaptor.capture());

        final OrderDto orderParam = orderCaptor.getValue();

        Assert.assertEquals(orderParam.getCurrency(), currency);
        Assert.assertEquals(orderParam.getPrice().doubleValue(), 10.0, 0);
    }
}
