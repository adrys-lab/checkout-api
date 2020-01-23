package com.adrian.rebollo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PageNotFoundException extends RuntimeException {

    public PageNotFoundException(String msg) {
        super(msg);
    }
}
