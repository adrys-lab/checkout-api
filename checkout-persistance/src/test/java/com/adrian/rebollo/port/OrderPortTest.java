package com.adrian.rebollo.port;

import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.entity.order.Order;
import com.adrian.rebollo.port.mapper.OrderBiMapper;
import com.adrian.rebollo.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderPortTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final int CHUNK = 20;
    private OrderPort orderPort;

    @Captor
    private ArgumentCaptor<Pageable> pageCaptor;
    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        orderPort = new OrderPortImpl(orderRepository, new OrderBiMapper(), CHUNK);
    }

    @Test
    void testPaginates() {

        Mockito.when(orderRepository.findByPlacedDateAfter(Mockito.any(), Mockito.any())).thenReturn(Mockito.mock(Page.class));

        orderPort.get(NOW);

        Mockito.verify(orderRepository).findByPlacedDateAfter(eq(NOW), pageCaptor.capture());

        Assert.assertEquals(CHUNK, pageCaptor.getValue().getPageSize());
    }

    @Test
    void createCorrectFields() {

        OrderDto orderDto = new OrderDto(UUID.randomUUID(), LocalDateTime.now(), BigDecimal.TEN, "USD", Collections.emptyList());
        Order order = new Order(LocalDateTime.now(), BigDecimal.TEN, "USD", Collections.emptyList());
        Mockito.when(orderRepository.save(Mockito.any())).thenReturn(order);

        orderPort.create(orderDto);

        Mockito.verify(orderRepository).save(orderCaptor.capture());

        Order argOrder = orderCaptor.getValue();

        Assert.assertEquals(argOrder.getCurrency(), orderDto.getCurrency());
        Assert.assertEquals(argOrder.getPrice().doubleValue(), orderDto.getPrice().doubleValue(), 1.0);
        Assert.assertEquals(argOrder.getPlacedDate(), orderDto.getPlacedDate());
    }
}