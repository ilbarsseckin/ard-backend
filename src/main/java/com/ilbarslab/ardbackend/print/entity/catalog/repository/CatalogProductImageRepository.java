package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatalogProductImageRepository extends JpaRepository<CatalogProductImage, UUID> {
    List<CatalogProductImage> findByProductIdOrderBySortOrderAsc(UUID productId);
    void deleteByProductId(UUID productId);
}
