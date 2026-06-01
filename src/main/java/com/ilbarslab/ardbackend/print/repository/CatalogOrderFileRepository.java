package com.ilbarslab.ardbackend.print.repository;


import com.ilbarslab.ardbackend.print.entity.CatalogOrderFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CatalogOrderFileRepository extends JpaRepository<CatalogOrderFile, UUID> {

    List<CatalogOrderFile> findByOrderIdOrderByCreatedAtAsc(UUID orderId);

    long countByOrderId(UUID orderId);

    void deleteByOrderId(UUID orderId);
}
