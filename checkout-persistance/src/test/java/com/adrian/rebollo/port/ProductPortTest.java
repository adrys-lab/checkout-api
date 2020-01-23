package com.adrian.rebollo.port;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.entity.product.Product;
import com.adrian.rebollo.exception.UnexistingProduct;
import com.adrian.rebollo.port.mapper.ProductBiMapper;
import com.adrian.rebollo.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductPortTest {

    private static final UUID ID = UUID.randomUUID();
    private static final int OFFSET = 1;
    private static final int CHUNK = 20;

    private ProductPort productPort;

    @Captor
    private ArgumentCaptor<Pageable> pageCaptor;
    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productPort = new ProductPortImpl(productRepository, new ProductBiMapper(), CHUNK);
    }

    @Test
    void testPaginates() {

        Mockito.when(productRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Mockito.mock(Page.class));

        productPort.getAll(OFFSET);

        Mockito.verify(productRepository).findAll(pageCaptor.capture());

        Assert.assertEquals(CHUNK, pageCaptor.getValue().getPageSize());
        Assert.assertEquals(OFFSET, pageCaptor.getValue().getPageNumber());
    }

    @Test
    void testGetWhenNoProductsReturnEmpty() {

        Mockito.when(productRepository.getByProducts(Mockito.anyList())).thenReturn(Collections.emptyList());

        Assert.assertTrue(productPort.get(Collections.singletonList(UUID.randomUUID())).isEmpty());
    }

    @Test
    void testGetReturnsWell() {

        Product firstProduct = new Product(ID, 0, "name", 2.0, "EUR");
        Mockito.when(productRepository.getByProducts(Mockito.anyList())).thenReturn(Collections.singletonList(firstProduct));

        Optional<List<ExistingProductDto>> result = productPort.get(Collections.singletonList(UUID.randomUUID()));

        Assert.assertTrue(result.isPresent());
        List<ExistingProductDto> results = result.get();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(firstProduct.getId(), results.get(0).getId());
        Assert.assertEquals(firstProduct.getPrice().doubleValue(), results.get(0).getPrice(), 1.0);
        Assert.assertEquals(firstProduct.getCurrency(), results.get(0).getCurrency());
    }

    @Test
    void testThrowsWhenUpdateNotExists() {
        assertThrows(UnexistingProduct.class, () -> productPort.update(new ExistingProductDto(UUID.randomUUID(), "", 5.0, "USD")));
    }

    @Test
    void updatesCorrectFields() {

        Product firstProduct = new Product(ID, 0, "name", 2.0, "EUR");
        Mockito.when(productRepository.findById(Mockito.any())).thenReturn(Optional.of(firstProduct));
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(firstProduct);

        productPort.update(new ExistingProductDto(UUID.randomUUID(), "name", 5.0, "USD"));

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());

        Assert.assertEquals(productArgumentCaptor.getValue(), firstProduct);
    }

    @Test
    void createCorrectFields() {

        Product firstProduct = new Product(ID, 0, "name", 2.0, "EUR");
        Mockito.when(productRepository.save(Mockito.any())).thenReturn(firstProduct);

        ProductDto productDto = new ProductDto("name", 5.0, "USD");

        productPort.create(productDto);

        Mockito.verify(productRepository).save(productArgumentCaptor.capture());

        Product product = productArgumentCaptor.getValue();

        Assert.assertEquals(product.getCurrency(), productDto.getCurrency());
        Assert.assertEquals(product.getPrice().doubleValue(), productDto.getPrice(), 1.0);
        Assert.assertEquals(product.getName(), productDto.getName());
    }

    @Test
    void getUnexistingReturnsEmpty() {
        Assert.assertTrue(productPort.get(UUID.randomUUID()).isEmpty());
    }
}
