package com.adrian.rebollo.port;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.adrian.rebollo.dto.order.OrderDto;

public interface OrderPort {

    /**
     * Create a new Order.
     *
     * @param orderDto to create
     * @return the new persisted entity
     */
    Optional<OrderDto> create(final OrderDto orderDto);

    /**
     * Given a date, return the orders which date is after the given one.
     *
     * @param date within the order must belong.
     * @return a Pagination of orders limited by offset & chunkSize
     */
    Page<OrderDto> get(final LocalDateTime date);
}
