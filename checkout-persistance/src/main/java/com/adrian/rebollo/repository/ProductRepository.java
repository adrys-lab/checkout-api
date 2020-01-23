package com.adrian.rebollo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adrian.rebollo.entity.product.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query(value= "select * from products where id in :productIds", nativeQuery = true)
    List<Product> getByProducts(@Param("productIds") List<UUID> productIds);
}
