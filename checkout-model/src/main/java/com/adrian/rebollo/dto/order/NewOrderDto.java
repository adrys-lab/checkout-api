package com.adrian.rebollo.dto.order;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewOrderDto implements Serializable {

    private String currency;
    private String email;
    private List<UUID> productList;
}
