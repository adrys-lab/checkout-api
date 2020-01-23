package com.adrian.rebollo.port;

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

import com.adrian.rebollo.port.ProductPort;
import com.adrian.rebollo.port.ProductPortImpl;
import com.adrian.rebollo.port.mapper.ProductBiMapper;
import com.adrian.rebollo.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductPortTest {

    private static final int OFFSET = 1;
    private static final int CHUNK = 20;

    private ProductPort productPort;

    @Captor
    private ArgumentCaptor<Pageable> pageCaptor;

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
}
