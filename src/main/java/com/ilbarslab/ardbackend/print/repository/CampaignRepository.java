package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {

    /** Admin için — tüm kampanyalar sıralı */
    List<Campaign> findAllByOrderBySortOrderAscCreatedAtAsc();

    /** Public için — sadece aktif kampanyalar (tarih kontrolü service'te) */
    List<Campaign> findByActiveTrueOrderBySortOrderAscCreatedAtAsc();
}
