package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.ProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductConfigRepository extends JpaRepository<ProductConfig, UUID> {
    List<ProductConfig> findByProductTypeIdOrderByDisplayOrderAsc(UUID productTypeId);
    List<ProductConfig> findByProductTypeSlug(String slug);
}
