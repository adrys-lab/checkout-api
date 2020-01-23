package com.adrian.rebollo.entity.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetail implements Serializable {

    private UUID id;
    private String name;
    private BigDecimal price;
    private String currency;

    public ProductDetail(final UUID id, final String name, final double price, final String currency) {
        this.id = id;
        this.name = name;
        this.price = BigDecimal.valueOf(price).setScale(4, RoundingMode.HALF_UP);
        this.currency = currency;
    }
}
