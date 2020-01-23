package com.adrian.rebollo.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.entity.order.Order;
import com.adrian.rebollo.port.mapper.OrderBiMapper;
import com.adrian.rebollo.repository.OrderRepository;

@Service
public class OrderPortImpl implements OrderPort {

    private final OrderRepository orderRepository;
    private final OrderBiMapper orderBiMapper;
    private final int chunkSize;

    @Autowired
    public OrderPortImpl(final OrderRepository orderRepository,
                         final OrderBiMapper orderBiMapper,
                         @Value("${pagination.orders.chunksize}") final int chunkSize) {
        this.orderRepository = orderRepository;
        this.orderBiMapper = orderBiMapper;
        this.chunkSize = chunkSize;
    }

    @Override
    public Optional<OrderDto> create(final OrderDto orderDto) {
        final Order order = orderBiMapper.toEntity(orderDto);
        return orderBiMapper.toDomain(Optional.of(orderRepository.save(order)));
    }

    @Override
    public Page<OrderDto> get(final LocalDateTime date) {

        final Pageable pageable = PageRequest.of(0, chunkSize, Sort.by("placedDate").ascending());
        final Page<Order> orderPage = orderRepository.findByPlacedDateAfter(date, pageable);

        final List<OrderDto> products = orderPage
                .get()
                .map(orderBiMapper::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(products, pageable, orderPage.getTotalElements());
    }
}
