package com.ilbarslab.ardbackend.print.entity.catalog.entity;

public enum CatalogPaymentStatus {
    PENDING,    // Ödeme başlatılmadı
    PROCESSING, // 3DS başlatıldı, callback bekleniyor
    PAID,       // Başarıyla ödendi
    FAILED,     // Başarısız
    REFUNDED    // İade edildi
}
