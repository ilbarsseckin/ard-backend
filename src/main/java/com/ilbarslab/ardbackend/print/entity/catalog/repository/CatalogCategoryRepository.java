package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatalogCategoryRepository extends JpaRepository<CatalogCategory, UUID> {
    Optional<CatalogCategory> findBySlug(String slug);
    List<CatalogCategory> findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
    List<CatalogCategory> findByParentIdOrderBySortOrderAsc(UUID parentId);
    List<CatalogCategory> findByActiveTrueOrderBySortOrderAsc();
}
