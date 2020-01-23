package com.adrian.rebollo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.adrian.rebollo.dto.product.ProductDto;
import lombok.Getter;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST, code = HttpStatus.BAD_REQUEST, reason = "Impossible create new product.")
public class ExceptionCreateProduct extends ApiInternalException {

    public ExceptionCreateProduct(final ProductDto productDto) {
        super(String.valueOf(productDto));
    }
}
