package com.adrian.rebollo.port;

import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;

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

import com.adrian.rebollo.port.mapper.OrderBiMapper;
import com.adrian.rebollo.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderPortTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static final int CHUNK = 20;
    private OrderPort orderPort;

    @Captor
    private ArgumentCaptor<Pageable> pageCaptor;

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
}