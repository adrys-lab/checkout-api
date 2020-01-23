package com.adrian.rebollo.entity.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import com.adrian.rebollo.entity.PersistedEntity;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Order extends PersistedEntity {

    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    @Column(name = "placed_date", updatable = false, nullable = false)
    private LocalDateTime placedDate;

    @Column(name = "price", columnDefinition = "numeric", updatable = false, nullable = false)
    private BigDecimal price;

    @Column(name = "currency", updatable = false, nullable = false)
    private String currency;

    @Column(name = "email", updatable = false, nullable = false, unique = true)
    private String email;

    @Type(type = "jsonb")
    @Column(name = "product_list", columnDefinition = "jsonb", updatable = false, nullable = false)
    private List<ProductDetail> productList;

    public Order(final LocalDateTime placedDate, final BigDecimal price, String currency, String email, final List<ProductDetail> productList) {
        super(0);
        this.placedDate = placedDate;
        this.price = price;
        this.currency = currency;
        this.email = email;
        this.productList = productList;
    }
}
