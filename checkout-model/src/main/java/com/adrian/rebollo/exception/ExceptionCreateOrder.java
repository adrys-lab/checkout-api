package com.adrian.rebollo.exception;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.adrian.rebollo.dto.order.NewOrderDto;
import lombok.Getter;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST, code = HttpStatus.BAD_REQUEST, reason = "Impossible create new order.")
public class ExceptionCreateOrder extends ApiInternalException {

    public ExceptionCreateOrder(final NewOrderDto newOrderDto) {
        super("productIds: "
                .concat(newOrderDto.getProductList().stream().map(UUID::toString).collect(Collectors.joining(",")))
                .concat(", Currency: ")
                .concat(newOrderDto.getCurrency()));
    }
}
