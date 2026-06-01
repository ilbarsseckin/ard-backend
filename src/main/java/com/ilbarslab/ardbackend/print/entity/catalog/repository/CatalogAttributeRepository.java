package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatalogAttributeRepository extends JpaRepository<CatalogAttribute, UUID> {
    List<CatalogAttribute> findByCategoryIdOrderBySortOrderAsc(UUID categoryId);
}
