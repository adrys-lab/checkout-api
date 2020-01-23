package com.adrian.rebollo.controller.order;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adrian.rebollo.OrderService;
import com.adrian.rebollo.ProductService;
import com.adrian.rebollo.RestConstants;
import com.adrian.rebollo.controller.HomeController;
import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.exception.ExceptionCreateOrder;
import com.adrian.rebollo.exception.UnexistingProduct;

/**
 * Main implementation of Basket Controller Interface.
 */
@RestController
@RequestMapping(path = "/order",
        produces = RestConstants.APPLICATION_JSON_UTF_8)
public class OrderControllerImpl extends HomeController implements OrderController {

    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public OrderControllerImpl(final OrderService orderService, final ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @Override
    public Resource<OrderDto> create(final NewOrderDto newOrderDto) {
        final List<ExistingProductDto> products = productService.get(newOrderDto.getProductList()).orElseThrow(() -> new UnexistingProduct(newOrderDto.getProductList()));
        return new Resource<>(orderService.create(products, newOrderDto).orElseThrow(() -> new ExceptionCreateOrder(newOrderDto)));
    }

    @Override
    public Page<OrderDto> get(LocalDateTime fromDate) {
        return orderService.get(fromDate);
    }
}
