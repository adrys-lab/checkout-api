package com.adrian.rebollo.dto.product;

import java.io.Serializable;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProductDto implements Serializable {

    private String name;
    private double price;
    private String currency;
}
