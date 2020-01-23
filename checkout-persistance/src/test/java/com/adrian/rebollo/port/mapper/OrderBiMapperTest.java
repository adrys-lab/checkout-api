package com.adrian.rebollo.port.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.entity.order.Order;
import com.adrian.rebollo.entity.order.ProductDetail;

public class OrderBiMapperTest {

    private static final UUID ID = UUID.randomUUID();
    private static final int VERSION = 1;
    private static final String CURRENCY = "USD";
    private static final BigDecimal PRICE = BigDecimal.TEN;
    private static final List<ProductDetail> PRODUCT_DETAIL_LIST = Collections.singletonList(new ProductDetail(UUID.randomUUID(), "", PRICE, ""));
    private static final LocalDateTime CREATED = LocalDateTime.now();

    private OrderBiMapper biMapper;

    @BeforeEach
    void setup() {
        biMapper = new OrderBiMapper();
    }

    @Test
    void assertMappingToDomain() {

        final Order entity = new Order(CREATED, PRICE, CURRENCY, PRODUCT_DETAIL_LIST);
        OrderDto orderDto = biMapper.toDomain(entity);

        assertThat(orderDto.getPlacedDate()).isEqualTo(CREATED);
        assertThat(orderDto.getPrice()).isEqualTo(PRICE);
        assertThat(orderDto.getCurrency()).isEqualTo(CURRENCY);
        Assert.assertTrue(areEquals(orderDto.getProductList().get(0), PRODUCT_DETAIL_LIST.get(0)));
    }

    @Test
    void assertMappingToEntity() {

        OrderDto orderDto =
                OrderDto.builder()
                        .id(ID)
                        .price(PRICE)
                        .currency(CURRENCY)
                        .productList(Collections.singletonList(new ExistingProductDto(UUID.randomUUID(), "", PRICE.doubleValue(), "")))
                        .placedDate(CREATED)
                        .build();

        Order entity = biMapper.toEntity(orderDto);

        assertThat(entity.getPlacedDate()).isEqualTo(CREATED);
        assertThat(entity.getPrice()).isEqualTo(PRICE);
        Assert.assertTrue(areEquals(orderDto.getProductList().get(0), entity.getProductList().get(0)));
    }

    private boolean areEquals(ExistingProductDto existingProductDto, ProductDetail productDetail) {
        return existingProductDto.getCurrency().equals(productDetail.getCurrency()) &&
                existingProductDto.getPrice() == productDetail.getPrice().doubleValue() &&
                existingProductDto.getName().equals(productDetail.getName()) &&
                existingProductDto.getId().equals(productDetail.getId());
    }


}