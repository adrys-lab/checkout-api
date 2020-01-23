package com.adrian.rebollo.exception;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/*
* Checked Exceptions with only internal visibility -> not for end-user
*/
@Getter
public abstract class ApiInternalException extends RuntimeException {

    private final String detail;

    public ApiInternalException(final String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return StringUtils.defaultIfBlank(detail, StringUtils.EMPTY);
    }
}
