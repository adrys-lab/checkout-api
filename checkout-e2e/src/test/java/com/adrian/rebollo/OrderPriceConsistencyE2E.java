package com.adrian.rebollo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.adrian.rebollo.controller.order.OrderController;
import com.adrian.rebollo.dto.order.NewOrderDto;
import com.adrian.rebollo.dto.order.OrderDto;
import com.adrian.rebollo.entity.product.Product;
import com.adrian.rebollo.repository.ProductRepository;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@ExtendWith(PostgresContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestConfiguration.class, initializers = { PostgresContainerExtension.Initializer.class })
class OrderPriceConsistencyE2E {

    private static final UUID FIRST_ID = UUID.randomUUID();
    private static final UUID SECOND_ID = UUID.randomUUID();

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderController orderController;

    @Test
    @DirtiesContext
    void testOptimisticLockWhenViolatingVersion() {
        Product firstProduct = new Product(FIRST_ID, 0, "name", 20.0, "EUR");
        Product originalSecondProduct = new Product(SECOND_ID, 0, "name", 10.0, "EUR");

        firstProduct = productRepository.saveAndFlush(firstProduct);
        originalSecondProduct = productRepository.saveAndFlush(originalSecondProduct);

        Resource<OrderDto> firstOrder = orderController.create(new NewOrderDto("EUR", "email", Arrays.asList(firstProduct.getId(), originalSecondProduct.getId())));

        assertNotNull(firstOrder.getContent());

        OrderDto savedOrder = firstOrder.getContent();
        assertEquals(30.0, savedOrder.getPrice().doubleValue(), 1.0);
        assertEquals(savedOrder.getCurrency(), "EUR");

        Product updatedSecondProduct = new Product(SECOND_ID, 1, "anotherName", 5.0, "USD");

        productRepository.saveAndFlush(updatedSecondProduct);

        Page<OrderDto> anotherFirstOrder = orderController.get(LocalDateTime.of(2019, 10, 10, 10, 10));

        assertNotNull(anotherFirstOrder);
        assertNotNull(anotherFirstOrder.get());
        assertTrue(anotherFirstOrder.get().findFirst().isPresent());

        OrderDto retrievedOrder = anotherFirstOrder.get().findFirst().get();
        assertEquals(30.0, retrievedOrder.getPrice().doubleValue(), 1.0);
        assertEquals(retrievedOrder.getCurrency(), "EUR");
        assertTrue(retrievedOrder.getProductList().stream().anyMatch(prod -> prod.getPrice() == 20.0));
        assertTrue(retrievedOrder.getProductList().stream().anyMatch(prod -> prod.getPrice() == 10.0));
    }
}


