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

    /**
     * create a new order based on the given products list and the order dto.
     * Retry the action if some exception due to optimistic locking is thrown (max 5 times repeating after 1sec.)
     *
     * @param products
     * @param newOrderDto
     * @return the object built.
     */
    @Retryable(value = { ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    Optional<OrderDto> create(final List<ExistingProductDto> products, final NewOrderDto newOrderDto);

    /**
     * Retrieve a Page of order results which should be ordered after the given date.
     *
     * @param date from which return the orders.
     * @return a Pagination of orders.
     */
    Page<OrderDto> get(final LocalDateTime date);
}
