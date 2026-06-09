package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.CatalogOrderResponse;
import com.ilbarslab.ardbackend.print.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Admin bildirim servisi:
 * - Yeni sipariş → admin SMS + email
 * - Ödeme alındı → admin SMS
 * - SMS: NetGSM
 * - Email: EmailService üzerinden
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationService {

    private final SystemSettingRepository settingRepo;
    private final EmailService emailService;

    @Value("${netgsm.username}")
    private String netgsmUsername;

    @Value("${netgsm.password}")
    private String netgsmPassword;

    @Value("${netgsm.header}")
    private String netgsmHeader;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    // ─── Admin telefon numaraları — system_settings tablosundan ───

    /** admin_notification_phones key'i ile kayıtlı virgülle ayrılmış numaralar */
    private List<String> getAdminPhones() {
        return settingRepo.findById("admin_notification_phones")
            .map(s -> Arrays.stream(s.getValue().split(","))
                .map(String::trim)
                .filter(p -> !p.isBlank())
                .toList())
            .orElse(List.of());
    }

    /** admin_notification_emails key'i ile kayıtlı virgülle ayrılmış emailler */
    private List<String> getAdminEmails() {
        return settingRepo.findById("admin_notification_emails")
            .map(s -> Arrays.stream(s.getValue().split(","))
                .map(String::trim)
                .filter(e -> !e.isBlank())
                .toList())
            .orElse(List.of());
    }

    // ─────────────────────────────────────────────────
    // YENİ SİPARİŞ BİLDİRİMİ
    // ─────────────────────────────────────────────────

    @Async
    public void notifyNewOrder(CatalogOrderResponse order) {
        String msg = String.format(
            "YENI SIPARIS! #%s - %s - ₺%s - Tel:%s",
            order.getOrderNumber(),
            order.getCustomerName(),
            order.getTotalTl() != null ? order.getTotalTl().toPlainString() : "0",
            order.getCustomerPhone() != null ? order.getCustomerPhone() : "-"
        );

        // SMS
        getAdminPhones().forEach(phone -> sendSms(phone, msg));

        // Email
        getAdminEmails().forEach(email ->
            emailService.sendAdminNewOrder(email, order, frontendUrl)
        );
    }

    // ─────────────────────────────────────────────────
    // ÖDEME ALINDI BİLDİRİMİ
    // ─────────────────────────────────────────────────

    @Async
    public void notifyPaymentReceived(CatalogOrderResponse order) {
        String msg = String.format(
            "ODEME ALINDI! #%s - %s - ₺%s",
            order.getOrderNumber(),
            order.getCustomerName(),
            order.getTotalTl() != null ? order.getTotalTl().toPlainString() : "0"
        );
        getAdminPhones().forEach(phone -> sendSms(phone, msg));
    }

    // ─────────────────────────────────────────────────
    // GECİKME UYARISI — MÜŞTERİ + ADMİN
    // ─────────────────────────────────────────────────

    @Async
    public void notifyDelay(CatalogOrderResponse order, int daysPassed) {
        // Müşteriye email
        emailService.sendDelayAlert(order, daysPassed);

        // Müşteriye SMS
        if (order.getCustomerPhone() != null) {
            String custMsg = String.format(
                "Sayin %s, #%s nolu siparizinizde gecikme olustu. En kisa surede tamamlayacagiz. Ozur dileriz. baskiurunleri.com",
                firstName(order.getCustomerName()),
                order.getOrderNumber()
            );
            sendSms(order.getCustomerPhone(), custMsg);
        }

        // Admin'e SMS
        String adminMsg = String.format(
            "GECIKME UYARISI! #%s - %s - %d is gunu gecti - Tel:%s",
            order.getOrderNumber(),
            order.getCustomerName(),
            daysPassed,
            order.getCustomerPhone() != null ? order.getCustomerPhone() : "-"
        );
        getAdminPhones().forEach(phone -> sendSms(phone, adminMsg));
    }

    // ─────────────────────────────────────────────────
    // NetGSM SMS GÖNDER
    // ─────────────────────────────────────────────────

    public void sendSms(String phone, String message) {
        if (phone == null || phone.isBlank()) return;
        try {
            String cleanPhone = phone.replaceAll("[^0-9]", "");
            // 0 ile başlıyorsa 90 ekle
            if (cleanPhone.startsWith("0")) cleanPhone = "9" + cleanPhone;
            // 10 haneliyse 90 ekle
            if (cleanPhone.length() == 10) cleanPhone = "90" + cleanPhone;

            String encoded = java.net.URLEncoder.encode(message, "UTF-8");
            String urlStr = "https://api.netgsm.com.tr/sms/send/get?" +
                "usercode=" + netgsmUsername +
                "&password=" + netgsmPassword +
                "&gsmno=" + cleanPhone +
                "&message=" + encoded +
                "&msgheader=" + netgsmHeader +
                "&dil=TR";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            conn.disconnect();
            log.info("NetGSM SMS → {} | response: {}", cleanPhone, code);
        } catch (Exception e) {
            log.error("SMS gönderilemedi → {}: {}", phone, e.getMessage());
        }
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "Müşteri";
        return fullName.trim().split("\\s+")[0];
    }
}
