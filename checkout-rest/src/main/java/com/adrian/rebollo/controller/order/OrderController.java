package com.adrian.rebollo.controller.order;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.validator.annotation.ValidNewOrder;
import com.adrian.rebollo.validator.annotation.ValidTimePeriod;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * All operations with orders will be routed by this controller.
 *
 * Understanding methods as REST defines:
 * POST -> new resource added, fails if existing.
 * GET -> get a resource
 * DELETE -> remove a resource
 * <p/>
 */
@Validated
@Api(tags = {"Order Operations"}, description = "Offer order resources.")
public interface OrderController {

    @ApiOperation(value = "Create a new Order.")
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    Resource<OrderDto> create(@RequestBody
                              @ApiParam(value = "New order request with Array of product Ids and Currency with which calculate order price.")
                              @ValidNewOrder NewOrderDto newOrderDto);

    @ApiOperation(value = "Get all Orders within a given time period.")
    @GetMapping("/{timePeriod}")
    @ResponseStatus(HttpStatus.OK)
    Page<OrderDto> get(
            @ApiParam(value = "Date within orders must belong (format yyyy-MM-ddTHH:mm:ss).", example = "2020-10-30T22:30:10")
            @PathVariable(value="timePeriod")
            @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
            @ValidTimePeriod LocalDateTime timePeriod);

}
