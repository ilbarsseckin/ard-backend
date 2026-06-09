package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.CatalogPaymentInitiateRequest;
import com.ilbarslab.ardbackend.print.dto.response.CatalogPaymentResponse;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.iyzipay.request.CreateThreedsPaymentRequest;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrder;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrderItem;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogPaymentStatus;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogOrderRepository;
import com.ilbarslab.ardbackend.print.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogPaymentService {

    private final CatalogOrderRepository orderRepository;
    private final CouponService couponService;
    private final EmailService emailService;
    private final AdminNotificationService adminNotification;
    private final CatalogOrderService catalogOrderService;

    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    @Value("${app.frontend-url:http://localhost:3001}")
    private String frontendUrl;

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    private Options getOptions() {
        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
        return options;
    }

    @Transactional
    public CatalogPaymentResponse initiatePayment(CatalogPaymentInitiateRequest request) {
        if (request.getOrderNumber() == null || request.getOrderNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderNumber zorunlu");
        }

        CatalogOrder order = orderRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));

        if (order.getPaymentStatus() == CatalogPaymentStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu sipariş zaten ödenmiş");
        }

        if (order.getTotalTl() == null || order.getTotalTl().signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sipariş tutarı geçersiz");
        }

        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setLocale(Locale.TR.getValue());
        paymentRequest.setConversationId(order.getId().toString());
        paymentRequest.setPrice(order.getTotalTl());
        paymentRequest.setPaidPrice(order.getTotalTl());
        paymentRequest.setCurrency(Currency.TRY.name());
        paymentRequest.setInstallment(1);
        paymentRequest.setBasketId(order.getId().toString());
        paymentRequest.setPaymentChannel(PaymentChannel.WEB.name());
        paymentRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());

        String callback = backendUrl + "/api/webhook/catalog-payment/callback";
        if (request.getCallbackUrl() != null && !request.getCallbackUrl().isBlank()) {
            callback = request.getCallbackUrl();
        }
        paymentRequest.setCallbackUrl(callback);

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(request.getCardHolderName());
        paymentCard.setCardNumber(request.getCardNumber());
        paymentCard.setExpireMonth(request.getExpireMonth());
        paymentCard.setExpireYear(request.getExpireYear());
        paymentCard.setCvc(request.getCvc());
        paymentCard.setRegisterCard(0);
        paymentRequest.setPaymentCard(paymentCard);

        String[] nameParts = order.getCustomerName().trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "Müşteri";

        Buyer buyer = new Buyer();
        buyer.setId(order.getUserId() != null
                ? order.getUserId().toString()
                : "anon-" + order.getId().toString().substring(0, 8));
        buyer.setName(firstName);
        buyer.setSurname(lastName);
        buyer.setEmail(order.getCustomerEmail() != null ? order.getCustomerEmail() : "noreply@baski.com");
        buyer.setGsmNumber(order.getCustomerPhone());
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress(order.getCustomerAddress());
        buyer.setCity(order.getCity() != null ? order.getCity() : "Istanbul");
        buyer.setCountry("Turkey");
        buyer.setIp("85.34.78.112");
        paymentRequest.setBuyer(buyer);

        Address shippingAddress = new Address();
        shippingAddress.setContactName(order.getCustomerName());
        shippingAddress.setCity(order.getCity() != null ? order.getCity() : "Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress(order.getCustomerAddress());
        paymentRequest.setShippingAddress(shippingAddress);
        paymentRequest.setBillingAddress(shippingAddress);

        List<BasketItem> basketItems = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (CatalogOrderItem item : order.getItems()) {
                BasketItem bi = new BasketItem();
                bi.setId(item.getId().toString());
                bi.setName(item.getProductName());
                bi.setCategory1(item.getCategoryName() != null ? item.getCategoryName() : "Katalog");
                bi.setItemType(BasketItemType.PHYSICAL.name());
                bi.setPrice(item.getPriceTl() != null ? item.getPriceTl() : BigDecimal.ZERO);
                basketItems.add(bi);
            }
        } else {
            BasketItem bi = new BasketItem();
            bi.setId(order.getId().toString());
            bi.setName("Katalog Siparişi");
            bi.setCategory1("Katalog");
            bi.setItemType(BasketItemType.PHYSICAL.name());
            bi.setPrice(order.getTotalTl());
            basketItems.add(bi);
        }
        paymentRequest.setBasketItems(basketItems);

        try {
            ThreedsInitialize threedsInitialize = ThreedsInitialize.create(paymentRequest, getOptions());
            log.info("Katalog Iyzico 3DS başlatıldı — sipariş: {} ({}), status: {}",
                    order.getOrderNumber(), order.getId(), threedsInitialize.getStatus());

            if ("success".equalsIgnoreCase(threedsInitialize.getStatus())) {
                order.setPaymentStatus(CatalogPaymentStatus.PROCESSING);
                orderRepository.save(order);

                return CatalogPaymentResponse.builder()
                        .status("pending_3ds")
                        .orderNumber(order.getOrderNumber())
                        .conversationId(order.getId().toString())
                        .htmlContent(threedsInitialize.getHtmlContent())
                        .success(true)
                        .build();
            } else {
                log.warn("Katalog Iyzico 3DS başarısız: {}", threedsInitialize.getErrorMessage());
                return CatalogPaymentResponse.builder()
                        .status("failed")
                        .orderNumber(order.getOrderNumber())
                        .errorMessage(threedsInitialize.getErrorMessage())
                        .success(false)
                        .build();
            }
        } catch (Exception e) {
            log.error("Katalog Iyzico hatası: {}", e.getMessage(), e);
            return CatalogPaymentResponse.builder()
                    .status("error")
                    .errorMessage("Ödeme başlatılamadı: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    @Transactional
    public boolean completePayment(String paymentId, String conversationData, String conversationId) {
        CatalogOrder order;
        try {
            order = orderRepository.findById(java.util.UUID.fromString(conversationId))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        } catch (IllegalArgumentException ex) {
            log.warn("Geçersiz conversationId: {}", conversationId);
            return false;
        }

        CreateThreedsPaymentRequest req = new CreateThreedsPaymentRequest();
        req.setLocale(Locale.TR.getValue());
        req.setConversationId(conversationId);
        req.setPaymentId(paymentId);
        req.setConversationData(conversationData);

        try {
            ThreedsPayment threedsPayment = ThreedsPayment.create(req, getOptions());
            log.info("Katalog 3DS tamamlama — sipariş: {}, status: {}",
                    order.getOrderNumber(), threedsPayment.getStatus());

            if ("success".equalsIgnoreCase(threedsPayment.getStatus())) {
                order.setPaymentStatus(CatalogPaymentStatus.PAID);
            // Ödeme emaili + admin SMS
            try {
                var orderResponse = catalogOrderService.getById(order.getId());
                emailService.sendPaymentSuccess(orderResponse);
                adminNotification.notifyPaymentReceived(orderResponse);
            } catch (Exception ex) {
                log.warn("Ödeme bildirimi gönderilemedi: {}", ex.getMessage());
            }
                order.setIyzicoPaymentId(paymentId);
                order.setIyzicoConversationData(conversationData);
                orderRepository.save(order);
                log.info("Katalog siparişi ÖDENDİ: {}", order.getOrderNumber());

                // ✨ YENİ — KUPON İŞLEMLERİ
                // 1) Uygulanan kuponu "kullanıldı" olarak işaretle
                // 2) Sipariş tutarına göre hediye kupon dağıt
                // NOT: Kupon hataları ödemeyi geri almaz — try/catch ile izole tutuluyor
                try {
                    if (order.getCouponCode() != null && !order.getCouponCode().isBlank()) {
                        couponService.markUsed(
                                order.getCouponCode(),
                                order.getUserId(),  // null olabilir (guest), markUsed null-safe
                                order.getId()
                        );
                        log.info("✓ Kupon kullanıldı: {} (sipariş {})",
                                order.getCouponCode(), order.getOrderNumber());
                    }

                    if (order.getUserId() != null && order.getTotalTl() != null) {
                        var issued = couponService.issueGiftCouponsIfEligible(
                                order.getUserId(),
                                order.getTotalTl()
                        );
                        if (!issued.isEmpty()) {
                            log.info("🎁 {} hediye kupon verildi (kullanıcı {}, sipariş {})",
                                    issued.size(), order.getUserId(), order.getOrderNumber());
                        }
                    }
                } catch (Exception couponEx) {
                    log.error("⚠️ Kupon işleme hatası (sipariş {}): {} — ödeme yine de geçerli",
                            order.getOrderNumber(), couponEx.getMessage(), couponEx);
                }

                return true;
            } else {
                order.setPaymentStatus(CatalogPaymentStatus.FAILED);
                orderRepository.save(order);
                log.warn("Katalog 3DS tamamlama başarısız: {} - {}",
                        order.getOrderNumber(), threedsPayment.getErrorMessage());
                return false;
            }
        } catch (Exception e) {
            log.error("Katalog 3DS tamamlama hatası: {}", e.getMessage(), e);
            order.setPaymentStatus(CatalogPaymentStatus.FAILED);
            orderRepository.save(order);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public String getOrderNumberById(String conversationId) {
        try {
            return orderRepository.findById(java.util.UUID.fromString(conversationId))
                    .map(CatalogOrder::getOrderNumber)
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}