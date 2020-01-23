package com.adrian.rebollo.entity.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.adrian.rebollo.entity.PersistedEntity;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "products")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product extends PersistedEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "currency", nullable = false)
    private String currency;

    public Product(final UUID id, int version, final String name, final double price, final String currency) {
        super(id, version);
        this.name = name;
        this.price = BigDecimal.valueOf(price).setScale(4, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public Product(final String name, final double price, final String currency) {
        super(0);
        this.name = name;
        this.price = BigDecimal.valueOf(price).setScale(4, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public void setPrice(final double price) {
        this.price = BigDecimal.valueOf(price).setScale(4, RoundingMode.HALF_UP);
    }
}
