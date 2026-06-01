package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogProductTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatalogProductTierRepository extends JpaRepository<CatalogProductTier, UUID> {
    List<CatalogProductTier> findByProductIdOrderByQtyAsc(UUID productId);
    void deleteByProductId(UUID productId);
}
