package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatalogProductAttributeValueRepository extends JpaRepository<CatalogProductAttributeValue, UUID> {
    List<CatalogProductAttributeValue> findByProductId(UUID productId);
    void deleteByProductId(UUID productId);

    void deleteByAttributeId(UUID attributeId);
    void deleteByOptionId(UUID optionId);

    // kullanım kontrolü için
    long countByAttributeId(UUID attributeId);
    long countByOptionId(UUID optionId);
}