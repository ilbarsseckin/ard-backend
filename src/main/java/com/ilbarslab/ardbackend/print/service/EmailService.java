package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.CatalogOrderItemResponse;
import com.ilbarslab.ardbackend.print.dto.response.CatalogOrderResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from-name:ARD Baskı}")
    private String fromName;

    @Value("${app.mail.from-address:your-gmail@gmail.com}")
    private String fromAddress;

    @Value("${app.mail.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.mail.logo-url:https://ardbaski.com/logo.png}")
    private String logoUrl;

    // ─────────────────────────────────────────────────
    // 1. SİPARİŞ ALINDI (ödeme bekleniyor)
    // ─────────────────────────────────────────────────

    @Async
    public void sendOrderCreated(CatalogOrderResponse order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) return;

        try {
            Context ctx = buildBaseContext();
            ctx.setVariable("customerName",  firstName(order.getCustomerName()));
            ctx.setVariable("orderNumber",   order.getOrderNumber());
            ctx.setVariable("totalTl",       formatTl(order.getTotalTl()));
            ctx.setVariable("paymentUrl",    frontendUrl + "/odeme-katalog?siparisNo=" + order.getOrderNumber());
            ctx.setVariable("trackUrl",      frontendUrl + "/siparis/" + order.getOrderNumber());
            ctx.setVariable("items",         order.getItems());
            ctx.setVariable("address",       order.getCustomerAddress());
            ctx.setVariable("city",          order.getCity());
            ctx.setVariable("orderDate",     formatDate(order.getCreatedAt()));
            ctx.setVariable("isPaid",        false);

            String html = templateEngine.process("email/order-confirmation", ctx);
            send(order.getCustomerEmail(), "Siparişiniz alındı — " + order.getOrderNumber(), html);

        } catch (Exception e) {
            log.error("Sipariş alındı emaili gönderilemedi ({}): {}", order.getOrderNumber(), e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────
    // 2. ÖDEME ONAYLANDI
    // ─────────────────────────────────────────────────

    @Async
    public void sendPaymentSuccess(CatalogOrderResponse order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) return;

        try {
            Context ctx = buildBaseContext();
            ctx.setVariable("customerName",  firstName(order.getCustomerName()));
            ctx.setVariable("orderNumber",   order.getOrderNumber());
            ctx.setVariable("totalTl",       formatTl(order.getTotalTl()));
            ctx.setVariable("trackUrl",      frontendUrl + "/siparis/" + order.getOrderNumber());
            ctx.setVariable("items",         order.getItems());
            ctx.setVariable("address",       order.getCustomerAddress());
            ctx.setVariable("city",          order.getCity());
            ctx.setVariable("orderDate",     formatDate(order.getCreatedAt()));
            ctx.setVariable("isPaid",        true);

            String html = templateEngine.process("email/order-confirmation", ctx);
            send(order.getCustomerEmail(), "Ödemeniz onaylandı — " + order.getOrderNumber(), html);

        } catch (Exception e) {
            log.error("Ödeme onay emaili gönderilemedi ({}): {}", order.getOrderNumber(), e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────
    // 3. SİPARİŞ DURUM DEĞİŞİKLİĞİ (kargo, üretim, tamamlandı...)
    // ─────────────────────────────────────────────────

    @Async
    public void sendStatusUpdate(CatalogOrderResponse order, String newStatus) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) return;

        StatusInfo info = StatusInfo.from(newStatus);
        if (info == null) return; // Email gönderilmeyecek statusler

        try {
            Context ctx = buildBaseContext();
            ctx.setVariable("customerName",   firstName(order.getCustomerName()));
            ctx.setVariable("orderNumber",    order.getOrderNumber());
            ctx.setVariable("totalTl",        formatTl(order.getTotalTl()));
            ctx.setVariable("trackUrl",       frontendUrl + "/siparis/" + order.getOrderNumber());
            ctx.setVariable("statusLabel",    info.label());
            ctx.setVariable("statusEmoji",    info.emoji());
            ctx.setVariable("statusMessage",  info.message());
            ctx.setVariable("statusColor",    info.color());
            ctx.setVariable("items",          order.getItems());

            String html = templateEngine.process("email/status-update", ctx);
            send(order.getCustomerEmail(), info.subject() + " — " + order.getOrderNumber(), html);

        } catch (Exception e) {
            log.error("Durum güncelleme emaili gönderilemedi ({}): {}", order.getOrderNumber(), e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────
    // 4. KARGO BİLDİRİMİ
    // ─────────────────────────────────────────────────

    @Async
    public void sendShipped(CatalogOrderResponse order, String trackingNumber, String cargoCompany) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) return;

        try {
            Context ctx = buildBaseContext();
            ctx.setVariable("customerName",   firstName(order.getCustomerName()));
            ctx.setVariable("orderNumber",    order.getOrderNumber());
            ctx.setVariable("totalTl",        formatTl(order.getTotalTl()));
            ctx.setVariable("trackUrl",       frontendUrl + "/siparis/" + order.getOrderNumber());
            ctx.setVariable("trackingNumber", trackingNumber);
            ctx.setVariable("cargoCompany",   cargoCompany != null ? cargoCompany : "Kargo firması");
            ctx.setVariable("address",        order.getCustomerAddress());
            ctx.setVariable("city",           order.getCity());
            ctx.setVariable("items",          order.getItems());

            String html = templateEngine.process("email/shipped", ctx);
            send(order.getCustomerEmail(), "Siparişiniz kargoya verildi — " + order.getOrderNumber(), html);

        } catch (Exception e) {
            log.error("Kargo emaili gönderilemedi ({}): {}", order.getOrderNumber(), e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────
    // 5. GUEST KULLANICI — ŞİFRE BİLDİRİMİ
    // ─────────────────────────────────────────────────

    @Async
    public void sendGuestWelcome(String email, String name, String plainPassword, String orderNumber) {
        try {
            Context ctx = buildBaseContext();
            ctx.setVariable("customerName",  firstName(name));
            ctx.setVariable("email",         email);
            ctx.setVariable("password",      plainPassword);
            ctx.setVariable("orderNumber",   orderNumber);
            ctx.setVariable("loginUrl",      frontendUrl + "/giris");
            ctx.setVariable("trackUrl",      frontendUrl + "/siparis/" + orderNumber);

            String html = templateEngine.process("email/guest-welcome", ctx);
            send(email, "Hoş geldiniz! Hesabınız oluşturuldu — ARD Baskı", html);

        } catch (Exception e) {
            log.error("Guest hoşgeldin emaili gönderilemedi ({}): {}", email, e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────
    // YARDIMCI — düşük seviye gönderme
    // ─────────────────────────────────────────────────

    private void send(String to, String subject, String htmlBody) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromAddress, fromName);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        mailSender.send(message);
        log.info("Email gönderildi → {} | {}", to, subject);
    }

    private Context buildBaseContext() {
        Context ctx = new Context(new Locale("tr", "TR"));
        ctx.setVariable("logoUrl",     logoUrl);
        ctx.setVariable("frontendUrl", frontendUrl);
        ctx.setVariable("year",        java.time.Year.now().getValue());
        return ctx;
    }

    // ─────────────────────────────────────────────────
    // FORMAT YARDIMCILARI
    // ─────────────────────────────────────────────────

    private String formatTl(BigDecimal amount) {
        if (amount == null) return "₺0";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("tr", "TR"));
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return "₺" + nf.format(amount);
    }

    private String formatDate(Instant instant) {
        if (instant == null) return "";
        return DateTimeFormatter
                .ofPattern("d MMMM yyyy, HH:mm", new Locale("tr", "TR"))
                .withZone(ZoneId.of("Europe/Istanbul"))
                .format(instant);
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "Değerli Müşteri";
        return fullName.trim().split("\\s+")[0];
    }

    // ─────────────────────────────────────────────────
    // STATUS → EMAIL BİLGİSİ
    // ─────────────────────────────────────────────────

    private record StatusInfo(String label, String emoji, String message, String subject, String color) {
        static StatusInfo from(String status) {
            return switch (status.toUpperCase()) {
                case "CONFIRMED"     -> new StatusInfo(
                        "Onaylandı", "✅",
                        "Siparişiniz incelendi ve onaylandı. Üretim sürecine alınmak üzere hazırlanıyor.",
                        "Siparişiniz onaylandı", "#22c55e");
                case "IN_PRODUCTION" -> new StatusInfo(
                        "Üretimde", "🖨️",
                        "Siparişiniz şu anda baskı sürecinde. En kısa sürede tamamlanıp kargoya verilecek.",
                        "Siparişiniz üretime alındı", "#3b82f6");
                case "READY"         -> new StatusInfo(
                        "Hazır", "📦",
                        "Siparişiniz tamamlandı ve kargoya verilmeye hazır. Kısa süre içinde kargoda olacak.",
                        "Siparişiniz hazır", "#8b5cf6");
                case "SHIPPED"       -> new StatusInfo(
                        "Kargoda", "🚚",
                        "Siparişiniz kargoya verildi ve yolda! Teslimat sürecini aşağıdan takip edebilirsiniz.",
                        "Siparişiniz kargoya verildi", "#f59e0b");
                case "DELIVERED"     -> new StatusInfo(
                        "Teslim Edildi", "🎉",
                        "Siparişiniz teslim edildi! Bizi tercih ettiğiniz için teşekkür ederiz.",
                        "Siparişiniz teslim edildi", "#10b981");
                case "CANCELLED"     -> new StatusInfo(
                        "İptal Edildi", "❌",
                        "Siparişiniz iptal edildi. Ödeme yaptıysanız iade 3-5 iş günü içinde gerçekleşecektir.",
                        "Siparişiniz iptal edildi", "#ef4444");
                default -> null; // PENDING için email gönderme
            };
        }
    }
}