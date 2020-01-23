package com.adrian.rebollo.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto implements Serializable {

    private UUID id;

    @JsonProperty("placedDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime placedDate;

    private BigDecimal price;
    private String currency;

    private List<ExistingProductDto> productList;
}
