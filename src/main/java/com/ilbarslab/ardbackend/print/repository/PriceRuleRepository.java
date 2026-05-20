package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.PriceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PriceRuleRepository extends JpaRepository<PriceRule, UUID> {
    List<PriceRule> findByProductTypeIdOrderByMinQtyAsc(UUID productTypeId);
    List<PriceRule> findByProductTypeSlug(String slug);
}
