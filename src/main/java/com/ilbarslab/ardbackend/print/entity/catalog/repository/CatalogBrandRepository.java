package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatalogBrandRepository extends JpaRepository<CatalogBrand, UUID> {
    Optional<CatalogBrand> findBySlug(String slug);
    List<CatalogBrand> findByActiveTrueOrderByNameAsc();
}
