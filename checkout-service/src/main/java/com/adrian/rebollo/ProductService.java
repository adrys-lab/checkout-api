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

    Optional<List<ExistingProductDto>> get(final List<UUID> productIds);

    Optional<ExistingProductDto> get(final UUID productId);

    Page<ExistingProductDto> getAll(final int offset);

    @Retryable(value = { ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    Optional<ExistingProductDto> create(final ProductDto productDto);

    @Retryable(value = { ObjectOptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public Optional<ExistingProductDto> update(final ExistingProductDto existingProductDto);
}
