package com.adrian.rebollo.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.adrian.rebollo.entity.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByPlacedDateAfter(final LocalDateTime date, final Pageable pageable);
}
