package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrder;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrderStatus;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogPaymentStatus;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogOrderRepository;
import com.ilbarslab.ardbackend.print.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Gecikmiş sipariş kontrolü:
 * Her gün 09:00'da çalışır.
 * Ödeme alınmış ama 3 iş gününden fazla bekleyen siparişleri tespit eder.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DelayAlertScheduler {

    private final CatalogOrderRepository orderRepo;
    private final AdminNotificationService adminNotification;
    private final SystemSettingRepository settingRepo;

    /** Gecikme eşiği — varsayılan 3 gün, settings tablosundan okunur */
    private int getDelayThresholdDays() {
        return settingRepo.findById("delay_alert_threshold_days")
            .map(s -> {
                try { return Integer.parseInt(s.getValue()); }
                catch (Exception e) { return 3; }
            })
            .orElse(3);
    }

    @Scheduled(cron = "0 0 9 * * *") // Her gün 09:00
    public void checkDelayedOrders() {
        int threshold = getDelayThresholdDays();
        Instant cutoff = Instant.now().minus(threshold, ChronoUnit.DAYS);

        // Ödeme alınmış ama hala PENDING veya CONFIRMED durumunda olan eski siparişler
        List<CatalogOrder> delayed = orderRepo
            .findByPaymentStatusAndStatusInAndCreatedAtBefore(
                CatalogPaymentStatus.PAID,
                List.of(CatalogOrderStatus.PENDING, CatalogOrderStatus.CONFIRMED),
                cutoff
            );

        log.info("Gecikme kontrolü: {} gecikmiş sipariş bulundu (eşik: {} gün)", delayed.size(), threshold);

        for (CatalogOrder order : delayed) {
            long daysPassed = ChronoUnit.DAYS.between(order.getCreatedAt(), Instant.now());
            try {
                // CatalogOrderResponse'a dönüştür (basit versiyon)
                var response = buildSimpleResponse(order);
                adminNotification.notifyDelay(response, (int) daysPassed);
                log.info("Gecikme uyarısı gönderildi: {} ({} gün)", order.getOrderNumber(), daysPassed);
            } catch (Exception e) {
                log.error("Gecikme uyarısı gönderilemedi {}: {}", order.getOrderNumber(), e.getMessage());
            }
        }
    }

    private com.ilbarslab.ardbackend.print.dto.response.CatalogOrderResponse buildSimpleResponse(CatalogOrder o) {
        return com.ilbarslab.ardbackend.print.dto.response.CatalogOrderResponse.builder()
            .id(o.getId())
            .orderNumber(o.getOrderNumber())
            .customerName(o.getCustomerName())
            .customerPhone(o.getCustomerPhone())
            .customerEmail(o.getCustomerEmail())
            .customerAddress(o.getCustomerAddress())
            .city(o.getCity())
            .totalTl(o.getTotalTl())
            .status(o.getStatus().name())
            .paymentStatus(o.getPaymentStatus().name())
            .createdAt(o.getCreatedAt())
            .build();
    }
}
