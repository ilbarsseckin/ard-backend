package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.entity.Order;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${resend.from-email}")
    private String fromEmail;

    @Value("${netgsm.username}")
    private String netgsmUsername;

    @Value("${netgsm.password}")
    private String netgsmPassword;

    @Value("${netgsm.header}")
    private String netgsmHeader;

    // Sipariş onay e-postası
    public void sendOrderConfirmation(Order order) {
        String subject = "Siparişiniz Alındı — #" + order.getId().toString().substring(0, 8).toUpperCase();
        String html = buildOrderConfirmationHtml(order);
        sendEmail(order.getUser().getEmail(), subject, html);
    }

    // Durum güncelleme e-postası
    public void sendStatusUpdate(Order order) {
        String statusText = getStatusText(order.getStatus());
        String subject = "Sipariş Durumu Güncellendi: " + statusText;
        String html = buildStatusUpdateHtml(order, statusText);
        sendEmail(order.getUser().getEmail(), subject, html);
    }

    // Operatör bildirimi
    public void sendOperatorNotification(Order order, String operatorEmail) {
        String subject = "Yeni Sipariş — #" + order.getId().toString().substring(0, 8).toUpperCase();
        String html = "<h2>Yeni sipariş geldi</h2>" +
                "<p>Sipariş ID: " + order.getId() + "</p>" +
                "<p>Müşteri: " + order.getUser().getName() + "</p>" +
                "<p>Tutar: ₺" + order.getTotalPrice() + "</p>" +
                "<p>Lütfen operatör panelinden kontrol ediniz.</p>";
        sendEmail(operatorEmail, subject, html);
    }

    // SMS gönder — Netgsm
    public void sendSms(String phone, String message) {
        try {
            String url = "https://api.netgsm.com.tr/sms/send/get?" +
                    "usercode=" + netgsmUsername +
                    "&password=" + netgsmPassword +
                    "&gsmno=" + phone.replace("+", "").replace(" ", "") +
                    "&message=" + java.net.URLEncoder.encode(message, "UTF-8") +
                    "&msgheader=" + netgsmHeader;

            java.net.URL netgsmUrl = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) netgsmUrl.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            log.info("Netgsm SMS gönderildi — telefon: {}, response: {}", phone, responseCode);
        } catch (Exception e) {
            log.error("SMS gönderme hatası: {}", e.getMessage());
        }
    }

    // Sipariş onay SMS
    public void sendOrderConfirmationSms(Order order) {
        if (order.getUser().getPhone() == null) return;
        String message = "Siparişiniz alındı. Sipariş No: " +
                order.getId().toString().substring(0, 8).toUpperCase() +
                " Tutar: ₺" + order.getTotalPrice();
        sendSms(order.getUser().getPhone(), message);
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            Resend resend = new Resend(resendApiKey);
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();
            resend.emails().send(params);
            log.info("E-posta gönderildi: {} — {}", to, subject);
        } catch (ResendException e) {
            log.error("E-posta gönderme hatası: {}", e.getMessage());
        }
    }

    private String buildOrderConfirmationHtml(Order order) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #1B4F8A;">Siparişiniz Alındı!</h2>
                    <p>Sayın %s,</p>
                    <p>Siparişiniz başarıyla alındı ve işleme konuldu.</p>
                    <table style="width:100%%; border-collapse: collapse;">
                        <tr style="background:#f5f5f5;">
                            <td style="padding:8px; border:1px solid #ddd;"><strong>Sipariş No</strong></td>
                            <td style="padding:8px; border:1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding:8px; border:1px solid #ddd;"><strong>Toplam Tutar</strong></td>
                            <td style="padding:8px; border:1px solid #ddd;">₺%s</td>
                        </tr>
                        <tr style="background:#f5f5f5;">
                            <td style="padding:8px; border:1px solid #ddd;"><strong>Teslimat Adresi</strong></td>
                            <td style="padding:8px; border:1px solid #ddd;">%s</td>
                        </tr>
                    </table>
                    <p style="margin-top:20px;">Siparişinizin durumunu takip edebilirsiniz.</p>
                    <p>Teşekkürler,<br><strong>Baskı Sistemi</strong></p>
                </body>
                </html>
                """.formatted(
                order.getUser().getName(),
                order.getId().toString().substring(0, 8).toUpperCase(),
                order.getTotalPrice(),
                order.getShippingAddress()
        );
    }

    private String buildStatusUpdateHtml(Order order, String statusText) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <h2 style="color: #1B4F8A;">Sipariş Durumu Güncellendi</h2>
                    <p>Sayın %s,</p>
                    <p>Siparişinizin durumu güncellendi.</p>
                    <table style="width:100%%; border-collapse: collapse;">
                        <tr style="background:#f5f5f5;">
                            <td style="padding:8px; border:1px solid #ddd;"><strong>Sipariş No</strong></td>
                            <td style="padding:8px; border:1px solid #ddd;">%s</td>
                        </tr>
                        <tr>
                            <td style="padding:8px; border:1px solid #ddd;"><strong>Yeni Durum</strong></td>
                            <td style="padding:8px; border:1px solid #ddd; color: #0F6E56;"><strong>%s</strong></td>
                        </tr>
                    </table>
                    <p style="margin-top:20px;">Teşekkürler,<br><strong>Baskı Sistemi</strong></p>
                </body>
                </html>
                """.formatted(
                order.getUser().getName(),
                order.getId().toString().substring(0, 8).toUpperCase(),
                statusText
        );
    }

    private String getStatusText(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Ödeme Bekleniyor";
            case PAID -> "Ödeme Alındı";
            case REVIEWING -> "İncelemede";
            case PRINTING -> "Baskıda";
            case SHIPPED -> "Kargoya Verildi";
            case COMPLETED -> "Tamamlandı";
            case CANCELLED -> "İptal Edildi";
        };
    }
}