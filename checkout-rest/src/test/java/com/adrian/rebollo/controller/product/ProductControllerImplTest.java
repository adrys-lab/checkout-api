package com.adrian.rebollo.controller.product;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adrian.rebollo.ProductService;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.exception.ExceptionCreateProduct;
import com.adrian.rebollo.exception.UnexistingProduct;

@ExtendWith(MockitoExtension.class)
class ProductControllerImplTest {

    private ProductController productController;

    @Mock
    private ProductService productService;

    @BeforeEach
    void init(){
        productController = new ProductControllerImpl(productService);
    }

    @Test
    void whenUnexistingGetThrows() {
        assertThrows(UnexistingProduct.class, () -> productController.get(UUID.randomUUID()));
    }

    @Test
    void whenPostUnexistingThrows() {
        final ProductDto productDto = new ProductDto();

        assertThrows(ExceptionCreateProduct.class, () -> productController.post(productDto));
    }

    @Test
    void whenPutUnexistingThrows() {
        final ProductDto productDto = new ProductDto();
        assertThrows(ExceptionCreateProduct.class, () -> productController.update(UUID.randomUUID(), productDto));
    }
}
