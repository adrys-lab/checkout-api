package com.adrian.rebollo.dto.product;

import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExistingProductDto extends ProductDto {

    private UUID id;

    public ExistingProductDto(final UUID id, final String name, final double price, final String currency) {
        super(name, price, currency);
        this.id = id;
    }
}
