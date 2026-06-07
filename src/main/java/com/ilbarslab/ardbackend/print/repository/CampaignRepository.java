package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {
    List<Campaign> findByActiveTrueOrderBySortOrderAscCreatedAtAsc();
    List<Campaign> findAllByOrderBySortOrderAscCreatedAtAsc();
    Optional<Campaign> findBySlugAndActiveTrue(String slug);
}
