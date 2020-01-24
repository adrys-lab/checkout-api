package com.adrian.rebollo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;

public interface ProductService {

    /**
     * get a List of products which Id must be container in the given productIds list.
     *
     * @param productIds products to retrieve
     * @return the list of existing products
     */
    Optional<List<ExistingProductDto>> get(final List<UUID> productIds);

    /**
     * return an existing product by the given ID.
     *
     * @param productId which retrieve.
     * @return the existng product.
     */
    Optional<ExistingProductDto> get(final UUID productId);

    /**
     * retrieve a pagination of products by the given offset.
     * return chunks of results (from offset) configurable through properties.
     *
     * @param offset from which start.
     * @return a Pagination of products limited by offset & chunkSize
     */
    Page<ExistingProductDto> getAll(final int offset);

    /**
     * Create a new product.
     * Retry the action if some exception due to optimistic locking is thrown (max 5 times repeating after 1sec.)
     *
     * @param productDto from which create the product
     * @return the new created product
     */
    @Retryable(value = { ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    Optional<ExistingProductDto> create(final ProductDto productDto);

    /**
     * Update an existing product.
     * Retry the action if some exception due to optimistic locking is thrown (max 5 times repeating after 1sec.)
     *
     * @param existingProductDto to be updated.
     * @return the updated product
     */
    @Retryable(value = { ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public Optional<ExistingProductDto> update(final ExistingProductDto existingProductDto);
}
