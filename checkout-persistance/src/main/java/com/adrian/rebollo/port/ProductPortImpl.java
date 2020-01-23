package com.adrian.rebollo.port;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.entity.product.Product;
import com.adrian.rebollo.exception.UnexistingProduct;
import com.adrian.rebollo.port.mapper.ProductBiMapper;
import com.adrian.rebollo.repository.ProductRepository;

@Component
public class ProductPortImpl implements ProductPort {

    private final ProductRepository productRepository;
    private final ProductBiMapper productBiMapper;
    private final int chunkSize;

    @Autowired
    public ProductPortImpl(final ProductRepository productRepository,
                           final ProductBiMapper productBiMapper,
                           @Value("${pagination.products.chunksize}") final int chunkSize) {
        this.productRepository = productRepository;
        this.productBiMapper = productBiMapper;
        this.chunkSize = chunkSize;
    }

    @Override
    public Optional<List<ExistingProductDto>> get(final List<UUID> productIds) {

        final List<Product>  products = productRepository.getByProducts(productIds);

        if(products.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(products
                .stream()
                .map(productBiMapper::toDomain)
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<ExistingProductDto> get(UUID productId) {
        return productBiMapper.toDomain(productRepository.findById(productId));
    }

    @Override
    public Page<ExistingProductDto> getAll(final int offset) {
        final Pageable pageable = PageRequest.of(offset, chunkSize, Sort.by("id").ascending());
        final Page<Product> productsPage = productRepository.findAll(pageable);

        final List<ExistingProductDto> products = productsPage
                .get()
                .map(productBiMapper::toDomain)
                .collect(Collectors.toList());

        return new PageImpl<>(products, pageable, productsPage.getTotalElements());
    }

    @Override
    public Optional<ExistingProductDto> create(final ProductDto productDto) {
        final Product product = new Product(productDto.getName(), productDto.getPrice(), productDto.getCurrency());
        return Optional.ofNullable(productBiMapper.toDomain(productRepository.save(product)));
    }

    @Override
    public Optional<ExistingProductDto> update(final ExistingProductDto existingProductDto) {

        final Product product = productRepository.findById(existingProductDto.getId()).orElseThrow(() -> new UnexistingProduct(existingProductDto.getId()));

        product.setName(existingProductDto.getName());
        product.setPrice(existingProductDto.getPrice());
        product.setCurrency(existingProductDto.getCurrency());

        return Optional.ofNullable(productBiMapper.toDomain(productRepository.save(product)));
    }
}
