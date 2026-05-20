package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.entity.*;
import com.ilbarslab.ardbackend.print.entity.enums.FileStatus;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.ilbarslab.ardbackend.print.entity.enums.PaymentStatus;
import com.ilbarslab.ardbackend.print.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FileEntityRepository fileEntityRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    // Sepetten sipariş oluştur
    @Transactional
    public Order createFromCart(String email, UUID addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Sepet bulunamadı"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Sepet boş");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Adres bulunamadı"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu adres size ait değil");
        }

        // Toplam fiyat
        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Toplam sayfa sayısı ve beyan kontrolü
        int totalPages = cart.getItems().stream()
                .mapToInt(i -> i.getFilePagesCount() != null ? i.getFilePagesCount() : 0)
                .sum();
        int totalDeclared = cart.getItems().stream()
                .mapToInt(CartItem::getDeclaredPrints)
                .sum();

        String shippingAddress = String.format("%s, %s %s/%s %s",
                address.getFullName(), address.getAddressLine(),
                address.getDistrict(), address.getCity(), address.getCountry());

        // Siparişi oluştur
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(total)
                .shippingAddress(shippingAddress)
                .pdfPageCount(totalPages)
                .declaredPrints(totalDeclared)
                .build();

        order = orderRepository.save(order);

        // Sepet kalemlerini sipariş kalemlerine dönüştür
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productType(cartItem.getProductType().getSlug())
                    .widthCm(cartItem.getWidthCm())
                    .heightCm(cartItem.getHeightCm())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build();

            orderItem = orderItemRepository.save(orderItem);

            // Dosya varsa OrderItem'a bağla — LOCKED durumda
            if (cartItem.getFileS3Key() != null) {
                FileEntity file = FileEntity.builder()
                        .orderItem(orderItem)
                        .s3Key(cartItem.getFileS3Key())
                        .originalName(cartItem.getFileOriginalName())
                        .pageCount(cartItem.getFilePagesCount() != null ? cartItem.getFilePagesCount() : 1)
                        .status(FileStatus.LOCKED)
                        .build();
                fileEntityRepository.save(file);
            }

            orderItems.add(orderItem);
        }

        // Boş ödeme kaydı oluştur
        Payment payment = Payment.builder()
                .order(order)
                .provider("iyzico")
                .status(PaymentStatus.PENDING)
                .amount(total)
                .build();
        paymentRepository.save(payment);

        // Sepeti temizle
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Sipariş oluşturuldu: {} — toplam: ₺{}", order.getId(), total);
        return order;
    }

    // Müşterinin siparişlerini listele
    public List<Order> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // Sipariş detayı
    public Order getOrder(String email, UUID orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu sipariş size ait değil");
        }
        return order;
    }
}
