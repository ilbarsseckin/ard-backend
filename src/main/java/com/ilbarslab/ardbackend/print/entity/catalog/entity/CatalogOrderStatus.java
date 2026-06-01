package com.ilbarslab.ardbackend.print.entity.catalog.entity;

public enum CatalogOrderStatus {
    PENDING,        // Yeni gelen sipariş
    CONFIRMED,      // Admin onayladı
    IN_PRODUCTION,  // Üretimde
    READY,          // Hazır, kargoya verilecek
    SHIPPED,        // Kargoda
    DELIVERED,      // Teslim edildi
    CANCELLED       // İptal edildi
}
