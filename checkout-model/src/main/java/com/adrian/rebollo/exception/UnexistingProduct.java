package com.adrian.rebollo.exception;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.Getter;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST, code = HttpStatus.BAD_REQUEST, reason = "The given Product/s ID/s not exist")
public class UnexistingProduct extends ApiInternalException {

    public UnexistingProduct(final String productId) {
        super(productId);
    }

    public UnexistingProduct(final UUID productId) {
        super(String.valueOf(productId));
    }

    public UnexistingProduct(final List<UUID> ids) {
        super(ids.stream().map(UUID::toString).collect(Collectors.joining(",")));
    }
}
