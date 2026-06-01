package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CatalogProductReviewRepository extends JpaRepository<CatalogProductReview, UUID> {

    // Bir ürünün yorumları (sadece onaylı)
    List<CatalogProductReview> findByProductIdAndApprovedTrueOrderByCreatedAtDesc(UUID productId);

    // Tüm yorumlar (admin için)
    List<CatalogProductReview> findAllByOrderByCreatedAtDesc();

    // Bir kullanıcı bu ürüne yorum yapmış mı?
    boolean existsByProductIdAndUserId(UUID productId, UUID userId);

    // Kullanıcı bu ürünü gerçekten satın almış mı?
    // (Sipariş statüsü iptal/bekliyor değilse satın almış sayılır)
    @Query(value = """
        SELECT COUNT(*) > 0 FROM catalog_orders o
        JOIN catalog_order_items i ON i.order_id = o.id
        WHERE o.user_id = :userId
          AND i.product_id = :productId
          AND o.status NOT IN ('PROCESSING', 'CANCELLED', 'FAILED', 'REFUNDED')
        """, nativeQuery = true)
    boolean hasUserPurchasedProduct(@Param("userId") UUID userId, @Param("productId") UUID productId);

    // Kullanıcının bu ürünü satın aldığı en son sipariş ID'si
    @Query(value = """
        SELECT o.id FROM catalog_orders o
        JOIN catalog_order_items i ON i.order_id = o.id
        WHERE o.user_id = :userId
          AND i.product_id = :productId
          AND o.status NOT IN ('PROCESSING', 'CANCELLED', 'FAILED', 'REFUNDED')
        ORDER BY o.created_at DESC
        LIMIT 1
        """, nativeQuery = true)
    UUID findLatestPurchaseOrderId(@Param("userId") UUID userId, @Param("productId") UUID productId);

    // Yorum sayısı (onaylı)
    long countByProductIdAndApprovedTrue(UUID productId);

    // Ortalama puan
    @Query("SELECT AVG(r.rating) FROM CatalogProductReview r WHERE r.product.id = :productId AND r.approved = true")
    Double findAverageRatingByProductId(@Param("productId") UUID productId);

    // Yıldız dağılımı [{rating, count}]
    @Query("SELECT r.rating, COUNT(r.id) FROM CatalogProductReview r WHERE r.product.id = :productId AND r.approved = true GROUP BY r.rating")
    List<Object[]> findRatingDistributionByProductId(@Param("productId") UUID productId);

    void deleteByProductId(UUID productId);
}
