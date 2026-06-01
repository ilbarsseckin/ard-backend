package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CatalogOrderItemRepository extends JpaRepository<CatalogOrderItem, UUID> {

    List<CatalogOrderItem> findByOrderId(UUID orderId);
}
