package com.adrian.rebollo.controller.product;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.adrian.rebollo.ProductService;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.exception.ExceptionCreateProduct;
import com.adrian.rebollo.exception.UnexistingProduct;

@ExtendWith(MockitoExtension.class)
class ProductControllerImplTest {

    private static final UUID ID = UUID.randomUUID();

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

    @Test
    void postResourceContainsId() {
        final ProductDto productDto = new ProductDto();
        final ExistingProductDto existingProductDto = new ExistingProductDto(ID, "", 5.0, "USD");

        Mockito.when(productService.create(productDto)).thenReturn(Optional.of(existingProductDto));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/products/");

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<URI> response = productController.post(productDto);

        Assert.assertTrue(response.toString().contains("/" + ID.toString()));
        Assert.assertTrue(response.getHeaders().get("Location").get(0).contains("/products/" + ID.toString()));
    }

    @Test
    void updateResourceContainsId() {
        final ProductDto productDto = new ProductDto();
        final ExistingProductDto existingProductDto = new ExistingProductDto(ID, "", 5.0, "USD");

        Mockito.when(productService.update(Mockito.any(ExistingProductDto.class))).thenReturn(Optional.of(existingProductDto));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/products/{id}");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ResponseEntity<URI> response = productController.update(ID, productDto);

        Assert.assertTrue(response.getHeaders().get("Location").get(0).contains("/products/" + ID.toString()));
    }
}
