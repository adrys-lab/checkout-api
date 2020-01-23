package com.adrian.rebollo.port.mapper;

import org.springframework.stereotype.Component;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.entity.product.Product;

@Component
public class ProductBiMapper implements BiMapper<Product, ExistingProductDto> {

    @Override
    public Product toEntity(ExistingProductDto productDto) {
        return new Product(productDto.getName(), productDto.getPrice(), productDto.getCurrency());
    }

    @Override
    public ExistingProductDto toDomain(Product product) {
        return ExistingProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice().doubleValue())
                .currency(product.getCurrency())
                .build();
    }
}
