package com.adrian.rebollo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.port.ProductPortImpl;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductPortImpl productPort;

    @Autowired
    public ProductServiceImpl(final ProductPortImpl productPort) {
        this.productPort = productPort;
    }

    @Transactional
    public Optional<List<ExistingProductDto>> get(final List<UUID> productIds) {
        return productPort.get(productIds);
    }

    @Transactional
    public Optional<ExistingProductDto> get(final UUID productId) {
        return productPort.get(productId);
    }

    @Transactional
    public Page<ExistingProductDto> getAll(final int offset) {
        return productPort.getAll(offset);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<ExistingProductDto> create(final ProductDto productDto) {
        return productPort.create(productDto);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<ExistingProductDto> update(final ExistingProductDto existingProductDto) {
        return productPort.update(existingProductDto);
    }

    /*
     Recover method when retries are exhausted for create a new product
     */
    @Recover
    public static Optional<ExistingProductDto> recoverCreate(final ProductDto productDto) {
        LOGGER.error("Retries exhausted by ObjectOptimisticLockingFailureException when Creating a new Product {}", productDto);
        return Optional.empty();
    }

    /*
     Recover method when retries are exhausted for update product
     */
    @Recover
    public static Optional<ExistingProductDto> recoverUpdate(final ExistingProductDto existingProductDto) {
        LOGGER.error("Retries exhausted by ObjectOptimisticLockingFailureException when Updating Product {}", existingProductDto);
        return Optional.empty();
    }
}
