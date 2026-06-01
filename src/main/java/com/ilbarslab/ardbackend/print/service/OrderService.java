package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.OrderDetailResponse;
import com.ilbarslab.ardbackend.print.dto.response.OrderItemDetailResponse;
import com.ilbarslab.ardbackend.print.entity.*;
import com.ilbarslab.ardbackend.print.entity.enums.FileStatus;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.ilbarslab.ardbackend.print.entity.enums.PaymentStatus;
import com.ilbarslab.ardbackend.print.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    /** Sepetten sipariş oluştur — Order entity döner (controller checkout için id ve total alır) */
    @Transactional
    public Order createFromCart(String email, UUID addressId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sepet bulunamadı"));

        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sepet boş");
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Adres bulunamadı"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu adres size ait değil");
        }

        BigDecimal total = cart.getItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalPages = cart.getItems().stream()
                .mapToInt(i -> i.getFilePagesCount() != null ? i.getFilePagesCount() : 0)
                .sum();
        int totalDeclared = cart.getItems().stream()
                .mapToInt(CartItem::getDeclaredPrints)
                .sum();

        String shippingAddress = String.format("%s, %s %s/%s %s",
                address.getFullName(), address.getAddressLine(),
                address.getDistrict(), address.getCity(), address.getCountry());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(total)
                .shippingAddress(shippingAddress)
                .pdfPageCount(totalPages)
                .declaredPrints(totalDeclared)
                .build();
        order = orderRepository.save(order);

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

    /** Müşterinin siparişleri — özet listesi (DTO) */
    @Transactional(readOnly = true)
    public List<OrderDetailResponse> getUserOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(o -> toDetail(o, user))
                .toList();
    }

    /** Sipariş detayı — DTO döner, lazy alanlar transaction içinde çözülür */
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrder(String email, UUID orderId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu sipariş size ait değil");
        }
        return toDetail(order, user);
    }

    // ───────────── private helpers ─────────────

    private OrderDetailResponse toDetail(Order order, User user) {
        List<OrderItemDetailResponse> itemDtos = order.getItems() == null
                ? List.of()
                : order.getItems().stream().map(this::toItemDetail).toList();

        boolean pageWarning = order.getPdfPageCount() != null
                && order.getDeclaredPrints() != null
                && order.getPdfPageCount() > order.getDeclaredPrints();

        return OrderDetailResponse.builder()
                .id(order.getId())
                .customerName(user.getName())
                .customerEmail(user.getEmail())
                .customerPhone(user.getPhone())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(order.getShippingAddress())
                .pdfPageCount(order.getPdfPageCount())
                .declaredPrints(order.getDeclaredPrints())
                .pageWarning(pageWarning)
                .createdAt(order.getCreatedAt())
                .items(itemDtos)
                .build();
    }

    private OrderItemDetailResponse toItemDetail(OrderItem it) {
        FileEntity f = it.getFile();   // OrderItem.file alanı — lazy ama @Transactional içinde
        BigDecimal lineTotal = it.getUnitPrice() == null ? BigDecimal.ZERO
                : it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity()));

        return OrderItemDetailResponse.builder()
                .id(it.getId())
                .productType(it.getProductType())
                .widthCm(it.getWidthCm())
                .heightCm(it.getHeightCm())
                .quantity(it.getQuantity())
                .unitPrice(it.getUnitPrice())
                .totalPrice(lineTotal)
                .fileS3Key(f != null ? f.getS3Key() : null)
                .fileOriginalName(f != null ? f.getOriginalName() : null)
                .filePageCount(f != null ? f.getPageCount() : null)
                .fileStatus(f != null && f.getStatus() != null ? f.getStatus().name() : null)
                .hasFile(f != null)
                .build();
    }
}