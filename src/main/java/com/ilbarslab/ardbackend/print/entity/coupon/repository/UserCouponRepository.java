package com.ilbarslab.ardbackend.print.entity.coupon.repository;

import com.ilbarslab.ardbackend.print.entity.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, UUID> {

    List<UserCoupon> findByUserIdAndUsedFalseOrderByCreatedAtDesc(UUID userId);

    List<UserCoupon> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByUserIdAndCouponIdAndUsedTrue(UUID userId, UUID couponId);

    Optional<UserCoupon> findByUserIdAndCouponIdAndUsedFalse(UUID userId, UUID couponId);
}
