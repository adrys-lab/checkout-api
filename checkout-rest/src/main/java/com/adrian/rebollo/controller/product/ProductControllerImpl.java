package com.adrian.rebollo.controller.product;

import java.net.URI;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.adrian.rebollo.ProductService;
import com.adrian.rebollo.RestConstants;
import com.adrian.rebollo.controller.HomeController;
import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.exception.ExceptionCreateProduct;
import com.adrian.rebollo.exception.UnexistingProduct;

/**
 * Main implementation of Basket Controller Interface.
 */
@RestController
@RequestMapping(path = "/product",
        produces = RestConstants.APPLICATION_JSON_UTF_8)
public class ProductControllerImpl extends HomeController implements ProductController {

    private final ProductService productService;

    @Autowired
    public ProductControllerImpl(final ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Page<ExistingProductDto> all(final int offset) {
        return productService.getAll(offset);
    }

    @Override
    public Resource<ExistingProductDto> get(final UUID productId) {
        return new Resource<>(productService.get(productId).orElseThrow(() -> new UnexistingProduct(productId)));
    }

    @Override
    public ResponseEntity<URI> post(final ProductDto productDto) {

        final ExistingProductDto newProduct = productService.create(productDto).orElseThrow(() -> new ExceptionCreateProduct(productDto));

        final URI resource = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newProduct.getId()).toUri();

        return ResponseEntity.created(resource).build();
    }

    @Override
    public ResponseEntity<URI> update(final UUID productId, final ProductDto productDto) {

        final ExistingProductDto existingProductDto = new ExistingProductDto(productId, productDto.getName(), productDto.getPrice(), productDto.getCurrency());
        final ExistingProductDto updatedProduct = productService.update(existingProductDto).orElseThrow(() -> new ExceptionCreateProduct(existingProductDto));

        final URI resource = ServletUriComponentsBuilder.fromCurrentRequest().build(updatedProduct.getId());

        return ResponseEntity.created(resource).build();
    }
}
