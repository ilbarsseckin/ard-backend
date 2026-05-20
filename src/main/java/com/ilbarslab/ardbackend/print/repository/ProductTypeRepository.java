package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {
    Optional<ProductType> findBySlug(String slug);
    List<ProductType> findByIsActiveTrue();
}
