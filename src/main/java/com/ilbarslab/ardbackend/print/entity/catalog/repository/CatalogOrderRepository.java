package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrder;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CatalogOrderRepository extends JpaRepository<CatalogOrder, UUID> {

    Optional<CatalogOrder> findByOrderNumber(String orderNumber);

    List<CatalogOrder> findAllByOrderByCreatedAtDesc();

    List<CatalogOrder> findByStatusOrderByCreatedAtDesc(CatalogOrderStatus status);

    List<CatalogOrder> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByStatus(CatalogOrderStatus status);
}
