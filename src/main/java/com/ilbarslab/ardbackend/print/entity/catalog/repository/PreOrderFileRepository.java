package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.PreOrderFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PreOrderFileRepository extends JpaRepository<PreOrderFile, UUID> {

    List<PreOrderFile> findByClaimedByOrderId(UUID orderId);

    /** Cleanup: 24 saatten eski claim edilmemiş dosyalar */
    List<PreOrderFile> findByClaimedByOrderIdIsNullAndCreatedAtBefore(Instant cutoff);
}
