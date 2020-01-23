package com.adrian.rebollo.util;

public enum ErrorMessages {

    PRODUCT_NULL("Product Can not be null."),
    PRODUCT_NAME_BLANK("Product Name could not be blank."),
    PRODUCT_PRICE_MIN("Product Price could not be lower than 0."),
    CURRENCY_BLANK("Currency Could not be Blank."),
    CURRENCY_NOT_EXISTS("Currency param does not exist."),
    ORDER_PRODUCTS_EMPTY("Order could not contain empty product lists."),
    ORDER_MAIL_EMPTY("Order could not contain empty or incorrect email."),
    ;

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
