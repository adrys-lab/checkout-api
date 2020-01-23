package com.adrian.rebollo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;

public interface OrderService {

    @Retryable(value = { ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    Optional<OrderDto> create(final List<ExistingProductDto> products, final NewOrderDto newOrderDto);

    Page<OrderDto> get(final LocalDateTime date);
}
