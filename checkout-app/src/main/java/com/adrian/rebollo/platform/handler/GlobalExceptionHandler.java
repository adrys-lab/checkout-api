package com.adrian.rebollo.platform.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.adrian.rebollo.error.ErrorInfo;
import com.adrian.rebollo.exception.ApiInternalException;


/**
 * Add a Global Exception handler to ensure there's no any Exception going out our platform.
 * We don't want the user to see any stacktrace or undesired exception message.
 */
@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_ERROR_MSG = "Oops ! There's a fatal error in our platform. Try again later.";

    /*
     * Handles unchecked and therefore critical exceptions
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> defaultErrorHandler(final WebRequest request, final Exception e) {
        return handleExceptionInternal(e, DEFAULT_ERROR_MSG, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /*
     * Handles checked exceptions to our Customized Error Information Response.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ApiInternalException.class)
    @ResponseBody ErrorInfo
    handleCheckedException(final WebRequest request, final ApiInternalException e) {
        final ResponseStatus annotatedException = e.getClass().getAnnotation(ResponseStatus.class);
        return new ErrorInfo(annotatedException.reason(), e.getDetail(), annotatedException.code().value());
    }

}
