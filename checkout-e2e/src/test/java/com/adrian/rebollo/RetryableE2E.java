package com.adrian.rebollo;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.adrian.rebollo.controller.product.ProductController;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.entity.product.Product;
import com.adrian.rebollo.exception.ExceptionCreateProduct;
import com.adrian.rebollo.port.ProductPort;
import com.adrian.rebollo.repository.ProductRepository;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@ExtendWith(PostgresContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestConfiguration.class, initializers = { PostgresContainerExtension.Initializer.class })
class RetryableE2E {

    private static final UUID ID = UUID.randomUUID();

    @Autowired
    private ProductController productController;

    @MockBean
    private ProductRepository productRepository;
    @SpyBean
    private ProductPort productPort;

    @Test
    void testCreateProductRetries() {

        final ProductDto productDto = new ProductDto("Name", 0.0, "USD");

        doThrow(new ObjectOptimisticLockingFailureException("Test", new RuntimeException()))
                .when(productRepository).save(Mockito.any(Product.class));

        try {
            productController.post(productDto);
        } catch (ExceptionCreateProduct e) {
        }

        Mockito.verify(productPort, timeout(10).times(5)).create(productDto);
    }

    @Test
    void testUpdateProductRetries() {

        final ProductDto productDto = new ProductDto("name", 0.0, "USD");
        final Product product = new Product("name", 0.0, "USD");

        Mockito.when(productRepository.findById(ID)).thenReturn(Optional.of(product));

        doThrow(new ObjectOptimisticLockingFailureException("Test", new RuntimeException()))
                .when(productRepository).save(Mockito.any(Product.class));

        try {
            productController.update(ID, productDto);
        } catch (ExceptionCreateProduct e) {
        }

        Mockito.verify(productPort, timeout(10).times(5)).update(Mockito.any(ExistingProductDto.class));
    }
}


