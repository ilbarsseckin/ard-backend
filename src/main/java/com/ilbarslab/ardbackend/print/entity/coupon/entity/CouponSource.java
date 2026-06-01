package com.ilbarslab.ardbackend.print.entity.coupon.entity;

public enum CouponSource {
    WELCOME,    // İlk ziyaret hediyesi
    GIFT,       // Sipariş eşiği aşıldığında otomatik verilen
    PROMO,      // Genel promosyon kodu
    MANUAL      // Admin tarafından elle atanmış
}
