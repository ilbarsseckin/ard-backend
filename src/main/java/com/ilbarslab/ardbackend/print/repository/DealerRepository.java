package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.Dealer;
import com.ilbarslab.ardbackend.print.entity.enums.DealerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DealerRepository extends JpaRepository<Dealer, UUID> {
    Optional<Dealer> findByUserId(UUID userId);
    Optional<Dealer> findByTaxNumber(String taxNumber);
    List<Dealer> findByStatus(DealerStatus status);
    boolean existsByTaxNumber(String taxNumber);
}