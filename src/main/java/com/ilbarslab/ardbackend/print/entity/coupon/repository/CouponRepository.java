package com.ilbarslab.ardbackend.print.entity.coupon.repository;

import com.ilbarslab.ardbackend.print.entity.coupon.entity.Coupon;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByCodeIgnoreCase(String code);

    List<Coupon> findByActiveTrueOrderByCreatedAtDesc();

    List<Coupon> findByActiveTrueAndAutoIssueOnFirstVisitTrue();

    /** Sipariş tutarı eşiği aşıldığında otomatik verilecek kuponları bul */
    List<Coupon> findByActiveTrueAndTypeAndAutoIssueOnOrderAmountIsNotNullAndAutoIssueOnOrderAmountLessThanEqual(
        CouponType type, BigDecimal orderAmount);
}
