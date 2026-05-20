package com.ilbarslab.ardbackend.print.service;

import com.iyzipay.Options;
import com.iyzipay.model.ThreedsPayment;
import com.iyzipay.request.CreateThreedsPaymentRequest;
import com.ilbarslab.ardbackend.print.entity.FileEntity;
import com.ilbarslab.ardbackend.print.entity.Order;
import com.ilbarslab.ardbackend.print.entity.Payment;
import com.ilbarslab.ardbackend.print.entity.enums.FileStatus;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.ilbarslab.ardbackend.print.entity.enums.PaymentStatus;
import com.ilbarslab.ardbackend.print.repository.FileEntityRepository;
import com.ilbarslab.ardbackend.print.repository.OrderRepository;
import com.ilbarslab.ardbackend.print.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final FileEntityRepository fileEntityRepository;
    private final NotificationService notificationService;
    private final OrderTrackingService orderTrackingService;

    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    private Options getOptions() {
        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
        return options;
    }

    @Transactional
    public String handle3dsCallback(String paymentId, String conversationId, String status) {
        log.info("3DS callback — paymentId: {}, conversationId: {}, status: {}",
                paymentId, conversationId, status);

        if (!"success".equalsIgnoreCase(status)) {
            log.warn("3DS başarısız — conversationId: {}", conversationId);
            handleFailedPayment(conversationId);
            return "failed";
        }

        CreateThreedsPaymentRequest request = new CreateThreedsPaymentRequest();
        request.setLocale("tr");
        request.setConversationId(conversationId);
        request.setPaymentId(paymentId);

        ThreedsPayment threedsPayment = ThreedsPayment.create(request, getOptions());

        if ("success".equalsIgnoreCase(threedsPayment.getStatus())) {
            handleSuccessfulPayment(conversationId, threedsPayment.getPaymentId());
            return "success";
        } else {
            log.error("iyzico ödeme onay hatası: {}", threedsPayment.getErrorMessage());
            handleFailedPayment(conversationId);
            return "failed";
        }
    }

    @Transactional
    public void handleSuccessfulPayment(String conversationId, String iyzicoPaymentId) {
        UUID orderId = UUID.fromString(conversationId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı: " + orderId));

        // Sipariş durumu güncelle
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Durum geçmişine kaydet
        orderTrackingService.updateStatus(orderId, OrderStatus.PAID, "Ödeme başarıyla alındı");

        // Ödeme kaydını güncelle
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if (!payments.isEmpty()) {
            Payment payment = payments.get(0);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setProviderRef(iyzicoPaymentId);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);
        }

        // Dosyaları kilidi aç
        order.getItems().forEach(item -> {
            FileEntity file = fileEntityRepository.findByOrderItemId(item.getId()).orElse(null);
            if (file != null) {
                file.setStatus(FileStatus.UNLOCKED);
                fileEntityRepository.save(file);
                log.info("Dosya kilidi açıldı: {}", file.getS3Key());
            }
        });

        // Bildirimler gönder
        try {
            notificationService.sendOrderConfirmation(order);
            notificationService.sendOrderConfirmationSms(order);
        } catch (Exception e) {
            log.error("Bildirim gönderme hatası: {}", e.getMessage());
        }

        log.info("Ödeme başarılı — sipariş: {}, iyzico: {}", orderId, iyzicoPaymentId);
    }

    @Transactional
    public void handleFailedPayment(String conversationId) {
        try {
            UUID orderId = UUID.fromString(conversationId);
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                orderTrackingService.updateStatus(orderId, OrderStatus.CANCELLED, "Ödeme başarısız");
            }
            List<Payment> payments = paymentRepository.findByOrderId(orderId);
            if (!payments.isEmpty()) {
                Payment payment = payments.get(0);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }
        } catch (Exception e) {
            log.error("Başarısız ödeme işleme hatası: {}", e.getMessage());
        }
    }
}