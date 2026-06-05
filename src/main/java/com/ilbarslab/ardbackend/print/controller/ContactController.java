package com.ilbarslab.ardbackend.print.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from-address:your-gmail@gmail.com}")
    private String fromAddress;

    @Value("${app.contact.to-email:${app.mail.from-address:your-gmail@gmail.com}}")
    private String toEmail;

    @PostMapping
    public ResponseEntity<?> send(@RequestBody Map<String, String> body) {
        String adSoyad = body.getOrDefault("adSoyad", "").trim();
        String email   = body.getOrDefault("email", "").trim();
        String telefon = body.getOrDefault("telefon", "").trim();
        String konu    = body.getOrDefault("konu", "Genel").trim();
        String mesaj   = body.getOrDefault("mesaj", "").trim();

        if (adSoyad.isEmpty() || email.isEmpty() || mesaj.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Ad soyad, email ve mesaj zorunludur."));
        }

        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper h = new MimeMessageHelper(mail, true, "UTF-8");
            h.setFrom(fromAddress);
            h.setTo(toEmail);
            h.setReplyTo(email);
            h.setSubject("📩 Yeni İletişim Formu: " + konu + " — " + adSoyad);
            h.setText("""
                <div style="font-family:system-ui,sans-serif;max-width:600px;margin:0 auto;padding:24px">
                  <h2 style="color:#F4821F;margin-bottom:20px">Yeni İletişim Formu</h2>
                  <table style="width:100%;border-collapse:collapse;font-size:14px">
                    <tr><td style="padding:8px 0;color:#888;width:120px">Ad Soyad</td><td style="padding:8px 0;font-weight:600">%s</td></tr>
                    <tr><td style="padding:8px 0;color:#888">E-posta</td><td style="padding:8px 0"><a href="mailto:%s" style="color:#F4821F">%s</a></td></tr>
                    <tr><td style="padding:8px 0;color:#888">Telefon</td><td style="padding:8px 0">%s</td></tr>
                    <tr><td style="padding:8px 0;color:#888">Konu</td><td style="padding:8px 0">%s</td></tr>
                  </table>
                  <div style="margin-top:20px;padding:16px;background:#f9f9f9;border-radius:12px;border-left:3px solid #F4821F">
                    <p style="margin:0;font-size:14px;line-height:1.7;color:#333">%s</p>
                  </div>
                </div>
                """.formatted(adSoyad, email, email, telefon.isEmpty() ? "—" : telefon, konu, mesaj.replace("\n", "<br>")),
                true);

            mailSender.send(mail);
            log.info("İletişim formu gönderildi — {}", email);
            return ResponseEntity.ok(Map.of("message", "Mesajınız iletildi, en kısa sürede dönüş yapacağız."));
        } catch (Exception e) {
            log.error("İletişim formu email hatası: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body(Map.of("message", "Email gönderilemedi, lütfen WhatsApp'tan ulaşın."));
        }
    }
}
