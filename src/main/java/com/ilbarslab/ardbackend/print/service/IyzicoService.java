package com.ilbarslab.ardbackend.print.service;

import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import com.ilbarslab.ardbackend.print.dto.request.PaymentInitiateRequest;
import com.ilbarslab.ardbackend.print.dto.response.PaymentResponse;
import com.ilbarslab.ardbackend.print.entity.Order;
import com.ilbarslab.ardbackend.print.entity.OrderItem;
import com.ilbarslab.ardbackend.print.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IyzicoService {

    private final OrderRepository orderRepository;

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
    @Transactional(readOnly = true)
    public PaymentResponse initiatePayment(PaymentInitiateRequest request, String userEmail) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setLocale(Locale.TR.getValue());
        paymentRequest.setConversationId(order.getId().toString());
        paymentRequest.setPrice(order.getTotalPrice());
        paymentRequest.setPaidPrice(order.getTotalPrice());
        paymentRequest.setCurrency(Currency.TRY.name());
        paymentRequest.setInstallment(1);
        paymentRequest.setBasketId(order.getId().toString());
        paymentRequest.setPaymentChannel(PaymentChannel.WEB.name());
        paymentRequest.setPaymentGroup(PaymentGroup.PRODUCT.name());
        paymentRequest.setCallbackUrl(request.getCallbackUrl() != null
                ? request.getCallbackUrl()
                : "http://localhost:8080/api/webhook/payment/callback");

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(request.getCardHolderName());
        paymentCard.setCardNumber(request.getCardNumber());
        paymentCard.setExpireMonth(request.getExpireMonth());
        paymentCard.setExpireYear(request.getExpireYear());
        paymentCard.setCvc(request.getCvc());
        paymentCard.setRegisterCard(0);
        paymentRequest.setPaymentCard(paymentCard);

        Buyer buyer = new Buyer();
        buyer.setId(order.getUser().getId().toString());
        buyer.setName(order.getUser().getName());
        buyer.setSurname("Kullanici");
        buyer.setEmail(order.getUser().getEmail());
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress(order.getShippingAddress());
        buyer.setCity("Istanbul");
        buyer.setCountry("Turkey");
        buyer.setIp("85.34.78.112");
        paymentRequest.setBuyer(buyer);

        Address shippingAddress = new Address();
        shippingAddress.setContactName(order.getUser().getName());
        shippingAddress.setCity("Istanbul");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress(order.getShippingAddress());
        paymentRequest.setShippingAddress(shippingAddress);
        paymentRequest.setBillingAddress(shippingAddress);

        List<BasketItem> basketItems = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                BasketItem basketItem = new BasketItem();
                basketItem.setId(item.getId().toString());
                basketItem.setName(item.getProductType());
                basketItem.setCategory1("Baski");
                basketItem.setItemType(BasketItemType.PHYSICAL.name());
                basketItem.setPrice(item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                basketItems.add(basketItem);
            }
        } else {
            BasketItem basketItem = new BasketItem();
            basketItem.setId(order.getId().toString());
            basketItem.setName("Baski Siparisi");
            basketItem.setCategory1("Baski");
            basketItem.setItemType(BasketItemType.PHYSICAL.name());
            basketItem.setPrice(order.getTotalPrice());
            basketItems.add(basketItem);
        }
        paymentRequest.setBasketItems(basketItems);

        try {
            ThreedsInitialize threedsInitialize = ThreedsInitialize.create(paymentRequest, getOptions());
            log.info("iyzico 3DS baslatildi — siparis: {}, status: {}",
                    order.getId(), threedsInitialize.getStatus());

            if ("success".equalsIgnoreCase(threedsInitialize.getStatus())) {
                return PaymentResponse.builder()
                        .status("pending_3ds")
                        .conversationId(order.getId().toString())
                        .htmlContent(threedsInitialize.getHtmlContent())
                        .success(true)
                        .build();
            } else {
                return PaymentResponse.builder()
                        .status("failed")
                        .errorMessage(threedsInitialize.getErrorMessage())
                        .success(false)
                        .build();
            }
        } catch (Exception e) {
            log.error("iyzico odeme hatasi: {}", e.getMessage(), e);
            return PaymentResponse.builder()
                    .status("error")
                    .errorMessage("Odeme baslatılamadi: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }
}