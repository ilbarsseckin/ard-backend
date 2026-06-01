package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogAttributeOption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface CatalogAttributeOptionRepository extends JpaRepository<CatalogAttributeOption, UUID> {
    List<CatalogAttributeOption> findByAttributeIdOrderBySortOrderAsc(UUID attributeId);
    void deleteByAttributeId(UUID attributeId);
}
