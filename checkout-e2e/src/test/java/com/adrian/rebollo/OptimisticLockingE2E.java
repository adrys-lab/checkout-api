package com.adrian.rebollo;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.adrian.rebollo.entity.product.Product;
import com.adrian.rebollo.repository.ProductRepository;

@SpringBootTest
@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@ExtendWith(PostgresContainerExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = TestConfiguration.class, initializers = { PostgresContainerExtension.Initializer.class })
class OptimisticLockingE2E {

    private static final UUID ID = java.util.UUID.randomUUID();

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DirtiesContext
    void testOptimisticLockWhenViolatingVersion() {
        Product firstProduct = new Product(ID, 0, "name", 2.0, "EUR");
        Product savedFirstProduct = productRepository.saveAndFlush(firstProduct);
        savedFirstProduct.setVersion(5);
        assertThrows(ObjectOptimisticLockingFailureException.class, () -> productRepository.saveAndFlush(savedFirstProduct));
    }
}


