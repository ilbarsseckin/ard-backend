package com.ilbarslab.ardbackend.print.entity.catalog.repository;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;


public interface CatalogProductRepository extends JpaRepository<CatalogProduct, UUID> {
    Optional<CatalogProduct> findBySlug(String slug);
    List<CatalogProduct> findByCategoryIdAndActiveTrueOrderBySortOrderAsc(UUID categoryId);
    List<CatalogProduct> findByCategoryIdOrderBySortOrderAsc(UUID categoryId);
    List<CatalogProduct> findByFeaturedTrueAndActiveTrueOrderBySortOrderAsc();
    List<CatalogProduct> findByBrandIdOrderBySortOrderAsc(UUID brandId);

    // ────────────────────────────────────────────────────────────────
    // En çok satan ürünler
    // ────────────────────────────────────────────────────────────────

    /**
     * En çok sipariş alan ürünlerin ID'lerini sıralı döndürür.
     * Native SQL — catalog_order_items tablosundaki count'a göre sıralanır.
     */
    @Query(value = """
        SELECT p.id
        FROM catalog_products p
        LEFT JOIN catalog_order_items oi ON oi.product_id = p.id
        WHERE p.active = true
        GROUP BY p.id
        ORDER BY COUNT(oi.id) DESC, p.created_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<UUID> findBestSellerProductIds(@Param("limit") int limit);

    /**
     * Her ürün için toplam sipariş sayısı.
     * Frontend'de "12 sipariş" gibi göstermek için kullanılır.
     * Dönen Object[]: [0]=productId UUID, [1]=orderCount Long
     */
    @Query(value = """
        SELECT p.id AS productId, COUNT(oi.id) AS orderCount
        FROM catalog_products p
        LEFT JOIN catalog_order_items oi ON oi.product_id = p.id
        WHERE p.active = true
        GROUP BY p.id
        """, nativeQuery = true)
    List<Object[]> countOrdersByProduct();
}