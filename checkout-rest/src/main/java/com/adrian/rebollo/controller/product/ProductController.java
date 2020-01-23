package com.adrian.rebollo.controller.product;

import java.net.URI;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.adrian.rebollo.dto.product.ExistingProductDto;
import com.adrian.rebollo.dto.product.ProductDto;
import com.adrian.rebollo.validator.annotation.ValidProductDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * All operations with a product will be routed by this controller.
 * Understanding methods as REST defines:
 * POST -> new resource added, fails if existing.
 * PUT -> can be used same as POST but PUT is usually used when the consumer knows exactly what URL or resource is creating/modifying (see update endpoint) by sending the concrete resource ID.
 * GET -> get a resource
 * DELETE -> remove a resource
 * <p/>
 */
@Validated
@Api(tags = {"Product Operations"}, description = "Offer products resources.")
public interface ProductController {

    @ApiOperation(value = "get all existing Products by offset in chunks of 10.")
    @GetMapping("/all/{offset}")
    @ResponseStatus(HttpStatus.OK)
    Page<ExistingProductDto> all(@PathVariable @ApiParam("Offset from which start the chunk of 10")
                                 @NotNull(message = "Offset parameter could not be null.")
                                 @Min(value = 0, message = "Minnimum Offset parameter is 0.") int offset);

    @ApiOperation(value = "Get a Product by the given Product Id.")
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    Resource<ExistingProductDto> get(
            @PathVariable
            @ApiParam("Id of the Product") UUID productId);

    @ApiOperation(value = "Add a new Product.")
    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<URI> post(@ValidProductDto
                             @RequestBody
                             @ApiParam("The new product") ProductDto productDto);

    @ApiOperation(value = "Update price or name of a Product for the given Product Id.")
    @PutMapping(path = ("/{productId}"), consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<URI> update(
            @PathVariable @ApiParam("Id of the Product") UUID productId,
            @ValidProductDto @RequestBody @ApiParam("Data with which update the Product by the given Id.") ProductDto existingProductDto);
}
