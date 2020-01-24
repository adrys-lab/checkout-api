package com.adrian.rebollo.controller.order;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adrian.rebollo.OrderService;
import com.adrian.rebollo.ProductService;
import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.exception.UnexistingProduct;

@ExtendWith(MockitoExtension.class)
class OrderControllerImplTest {

    private OrderController orderController;

    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;

    @BeforeEach
    void init(){
        orderController = new OrderControllerImpl(orderService, productService);
    }

    @Test
    void createWithUnexistingProductThrows() {
        final NewOrderDto newOrderDto = new NewOrderDto("USD", "email", Collections.singletonList(UUID.randomUUID()));
        assertThrows(UnexistingProduct.class, () -> orderController.create(newOrderDto));
    }

    @Test
    void whenExistingProductCallsWithThem() {

        final NewOrderDto newOrderDto = new NewOrderDto("USD", "email", Collections.singletonList(UUID.randomUUID()));

        final List<ExistingProductDto> existingProducts = Collections.singletonList(new ExistingProductDto());
        Mockito.when(productService.get(newOrderDto.getProductList())).thenReturn(Optional.of(existingProducts));
        Mockito.when(orderService.create(Mockito.anyList(), Mockito.any(NewOrderDto.class))).thenReturn(Optional.of(new OrderDto()));

        orderController.create(newOrderDto);

        Mockito.verify(orderService).create(existingProducts, newOrderDto);
    }

    @Test
    void whenSomeExistingAndSomeNotThrows() {

        final NewOrderDto newOrderDto = new NewOrderDto("USD", "email", Collections.singletonList(UUID.randomUUID()));

        final List<ExistingProductDto> existingProducts = Arrays.asList(new ExistingProductDto(), new ExistingProductDto());

        Mockito.when(productService.get(newOrderDto.getProductList())).thenReturn(Optional.of(existingProducts));

        assertThrows(UnexistingProduct.class, () -> orderController.create(newOrderDto));
    }

    @Test
    void whenFromDateCallsService() {

        LocalDateTime now = LocalDateTime.now();
        orderController.get(now);

        Mockito.verify(orderService).get(now);

    }
}
