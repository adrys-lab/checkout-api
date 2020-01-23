package com.adrian.rebollo.port.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.entity.product.Product;

public class ProductBiMapperTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE = BigDecimal.TEN;
    private static final String NAME = "Name";

    private ProductBiMapper biMapper;

    @BeforeEach
    void setup() {
        biMapper = new ProductBiMapper();
    }

    @Test
    void assertMappingToDomain() {

        final Product entity = new Product(NAME, PRICE, CURRENCY);
        ProductDto productDto = biMapper.toDomain(entity);

        assertThat(productDto.getCurrency()).isEqualTo(CURRENCY);
        assertThat(productDto.getPrice()).isEqualTo(PRICE.doubleValue());
        assertThat(productDto.getName()).isEqualTo(NAME);
    }

    @Test
    void assertMappingToEntity() {

        ExistingProductDto productDto =
                ExistingProductDto.builder()
                        .id(ID)
                        .price(PRICE.doubleValue())
                        .currency(CURRENCY)
                        .name(NAME)
                        .build();

        Product entity = biMapper.toEntity(productDto);

        assertThat(entity.getCurrency()).isEqualTo(CURRENCY);
        assertThat(entity.getName()).isEqualTo(NAME);
        assertThat(entity.getPrice().doubleValue()).isEqualTo(PRICE.doubleValue());
    }
}