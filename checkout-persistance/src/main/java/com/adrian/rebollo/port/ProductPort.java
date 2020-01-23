package com.adrian.rebollo.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;

public interface ProductPort {

    /**
     * given a product id, return the existing representation of the entity.
     * @param productId id
     * @return the product.
     */
    Optional<ExistingProductDto> get(UUID productId);

    /**
     * Given a list of Product Ids, return all the existing products.
     * This could not be paginated as what is intended is to get all products to calculate the price.
     *
     * @param productIds list of Product Ids
     * @return the total list of products
     */
    Optional<List<ExistingProductDto>> get(final List<UUID> productIds);

    /**
     * Given an offset from which start the pagination,
     * get all the Existing products in chunks.
     *
     * @param offset from which start
     * @return a Pagination of products limited by offset & chunkSize
     */
    Page<ExistingProductDto> getAll(final int offset);

    /**
     * Create a new Product entit   y.
     *
     * @param productDto from which create the new entity
     * @return the new persisted entity.
     */
    Optional<ExistingProductDto> create(final ProductDto productDto);

    /**
     * Update an existing product entity
     *
     * @param existingProductDto to update
     * @return the new updated entity
     */
    Optional<ExistingProductDto> update(final ExistingProductDto existingProductDto);
}
