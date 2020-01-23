package com.adrian.rebollo.port.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.entity.order.Order;
import com.adrian.rebollo.entity.order.ProductDetail;

@Component
public class OrderBiMapper implements BiMapper<Order, OrderDto> {

    @Override
    public Order toEntity(OrderDto orderDto) {
        return new Order(orderDto.getPlacedDate(),
                orderDto.getPrice(),
                orderDto.getCurrency(),
                orderDto.getProductList().stream().map(productDto -> new ProductDetail(productDto.getId(), productDto.getName(), productDto.getPrice(), productDto.getCurrency())).collect(Collectors.toList()));
    }

    @Override
    public OrderDto toDomain(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .placedDate(order.getPlacedDate())
                .price(order.getPrice())
                .currency(order.getCurrency())
                .productList(order.getProductList().stream().map(productDetail -> new ExistingProductDto(productDetail.getId(), productDetail.getName(), productDetail.getPrice().doubleValue(), productDetail.getCurrency())).collect(Collectors.toList()))
                .build();
    }
}
