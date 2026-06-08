package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.CatalogOrderItemResponse;
import com.ilbarslab.ardbackend.print.dto.response.CatalogOrderResponse;
import com.ilbarslab.ardbackend.print.entity.User;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.*;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.*;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.Coupon;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.CouponType;
import com.ilbarslab.ardbackend.print.entity.coupon.repository.CouponRepository;
import com.ilbarslab.ardbackend.print.entity.enums.Role;
import com.ilbarslab.ardbackend.print.repository.CatalogProductRepository;
import com.ilbarslab.ardbackend.print.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogOrderService {

    private final CatalogOrderRepository orderRepo;
    private final CatalogOrderItemRepository itemRepo;
    private final CatalogProductRepository productRepo;
    private final CatalogProductTierRepository tierRepo;
    private final CatalogProductImageRepository imageRepo;
    private final CatalogAttributeRepository attrRepo;
    private final CatalogAttributeOptionRepository optionRepo;
    private final PreOrderFileService preOrderFileService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CouponRepository couponRepo;

    private static final char[] PASSWORD_CHARS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789".toCharArray();
    private static final SecureRandom RAND = new SecureRandom();

    // ─────────── CREATE ───────────

    @Transactional
    public CatalogOrderResponse create(Map<String, Object> body, UUID userIdOrNull) {
        String customerName    = required(body, "customerName",    "Ad zorunlu");
        String customerPhone   = required(body, "customerPhone",   "Telefon zorunlu");
        String customerAddress = required(body, "customerAddress", "Adres zorunlu");
        String customerEmail   = asString(body.get("customerEmail"));
        String city            = asString(body.get("city"));
        String district        = asString(body.get("district"));
        String notes           = asString(body.get("notes"));

        Object rawItems = body.get("items");
        if (!(rawItems instanceof List<?> itemsList) || itemsList.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "En az 1 ürün gerekli");

        boolean guestAccountCreated = false;
        String guestPlainPassword   = null;

        if (userIdOrNull == null && customerEmail != null && !customerEmail.isBlank()) {
            String normalizedEmail = customerEmail.toLowerCase().trim();
            Optional<User> existing = userRepository.findByEmail(normalizedEmail);
            if (existing.isEmpty()) {
                String plainPassword = generateRandomPassword();
                User newUser = User.builder()
                        .email(normalizedEmail)
                        .password(passwordEncoder.encode(plainPassword))
                        .name(customerName)
                        .phone(customerPhone)
                        .role(Role.CUSTOMER)
                        .emailVerified(false)
                        .build();
                User saved = userRepository.save(newUser);
                log.info("🔑 [GUEST-CHECKOUT] Yeni kullanıcı oluşturuldu: {}", normalizedEmail);
                userIdOrNull        = saved.getId();
                guestAccountCreated = true;
                guestPlainPassword  = plainPassword;
            }
        }

        BigDecimal kur = getCurrentKur();

        CatalogOrder order = CatalogOrder.builder()
                .orderNumber(generateOrderNumber())
                .customerName(customerName)
                .customerPhone(customerPhone)
                .customerEmail(customerEmail)
                .customerAddress(customerAddress)
                .city(city)
                .district(district)
                .notes(notes)
                .usdKurAtOrder(kur)
                .status(CatalogOrderStatus.PENDING)
                .paymentStatus(CatalogPaymentStatus.PENDING)
                .userId(userIdOrNull)
                .build();
        order = orderRepo.save(order);

        BigDecimal subtotalUsd = BigDecimal.ZERO;
        BigDecimal totalTl     = BigDecimal.ZERO;
        List<UUID> allDesignFileIds      = new ArrayList<>();
        List<String> designSupportNotes  = new ArrayList<>();

        for (Object rawItem : itemsList) {
            if (!(rawItem instanceof Map<?,?> item)) continue;

            UUID productId = parseUuid(item.get("productId"));
            UUID tierId    = parseUuid(item.get("tierId"));
            if (productId == null || tierId == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Her üründe productId ve tierId zorunlu");

            CatalogProduct product = productRepo.findById(productId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ürün bulunamadı: " + productId));
            CatalogProductTier tier = tierRepo.findById(tierId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fiyat baremi bulunamadı: " + tierId));

            if (!tier.getProduct().getId().equals(productId))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tier bu ürüne ait değil");

            BigDecimal priceTl  = parseDecimal(item.get("priceTl"));
            BigDecimal priceUsd = parseDecimal(item.get("priceUsd"));

            if (priceTl == null || priceTl.signum() <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "priceTl zorunlu ve pozitif olmalı (" + product.getName() + ")");
            if (priceUsd == null || priceUsd.signum() <= 0)
                priceUsd = tier.getPriceUsd();

            String mainImage = imageRepo.findByProductIdOrderBySortOrderAsc(productId).stream()
                    .findFirst().map(CatalogProductImage::getUrl).orElse(null);

            String attrSnapshot = buildAttributesSnapshot(item.get("attributes"));

            CatalogOrderItem oi = CatalogOrderItem.builder()
                    .order(order)
                    .productId(product.getId())
                    .productSlug(product.getSlug())
                    .productName(product.getName())
                    .mainImageUrl(mainImage)
                    .categoryId(product.getCategory().getId())
                    .categorySlug(product.getCategory().getSlug())
                    .categoryName(product.getCategory().getName())
                    .tierId(tier.getId())
                    .tierQty(tier.getQty())
                    .priceUsd(priceUsd)
                    .priceTl(priceTl)
                    .attributesSnapshot(attrSnapshot)
                    .build();

            itemRepo.save(oi);
            subtotalUsd = subtotalUsd.add(priceUsd);
            totalTl     = totalTl.add(priceTl);

            Object rawFileIds = item.get("designFileIds");
            if (rawFileIds instanceof List<?> fileList) {
                for (Object fid : fileList) {
                    UUID fileUuid = parseUuid(fid);
                    if (fileUuid != null) allDesignFileIds.add(fileUuid);
                }
            }

            Object rawSupport = item.get("designSupport");
            if (rawSupport instanceof Map<?,?> supportMap) {
                Object rawNotes = supportMap.get("notes");
                if (rawNotes != null && !rawNotes.toString().isBlank())
                    designSupportNotes.add("• " + product.getName() + " — " + rawNotes.toString().trim());
            }
        }

        if (!designSupportNotes.isEmpty()) {
            String supportBlock = "🎨 Tasarım Desteği İstenenler:\n" + String.join("\n", designSupportNotes);
            String existing = order.getNotes();
            order.setNotes(existing == null || existing.isBlank() ? supportBlock : existing + "\n\n" + supportBlock);
        }

        order.setSubtotalUsd(subtotalUsd);
        order.setSubtotalTl(totalTl);
        order.setTotalTl(totalTl);
        order.setDiscountAmountTl(BigDecimal.ZERO);
        order = orderRepo.save(order);

        if (!allDesignFileIds.isEmpty()) {
            preOrderFileService.claimByOrder(allDesignFileIds, order.getId());
            log.info("Siparişe {} tasarım dosyası bağlandı", allDesignFileIds.size());
        }

        log.info("Katalog siparişi oluşturuldu: {} - {} ürün - ₺{} (guest={})",
                order.getOrderNumber(), itemsList.size(), totalTl, guestAccountCreated);

        CatalogOrderResponse response = toResponse(order);
        response.setGuestAccountCreated(guestAccountCreated);

        emailService.sendOrderCreated(response);
        if (guestAccountCreated && guestPlainPassword != null && customerEmail != null)
            emailService.sendGuestWelcome(customerEmail, customerName, guestPlainPassword, order.getOrderNumber());

        return response;
    }

    // ─────────── COUPON ───────────

    @Transactional
    public CatalogOrderResponse applyCoupon(String orderNumber, String code) {
        CatalogOrder order = orderRepo.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));

        if (order.getPaymentStatus() == CatalogPaymentStatus.PAID)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ödenmiş siparişe kupon uygulanamaz");

        Coupon coupon = couponRepo.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon bulunamadı veya geçersiz"));

        if (!Boolean.TRUE.equals(coupon.getActive()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon aktif değil");

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon henüz aktif değil");
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon süresi dolmuş");
        if (coupon.getMaxUsage() != null && coupon.getCurrentUsage() != null
                && coupon.getCurrentUsage() >= coupon.getMaxUsage())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon kullanım limiti doldu");

        BigDecimal subtotal = order.getSubtotalTl() != null
                ? order.getSubtotalTl()
                : (order.getTotalTl() != null ? order.getTotalTl() : BigDecimal.ZERO);

        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Minimum sipariş tutarı ₺" + coupon.getMinOrderAmount().toPlainString());

        // İndirim hesapla
        BigDecimal discount = BigDecimal.ZERO;
        CouponType type = coupon.getType();
        if (type == CouponType.PERCENT && coupon.getDiscountPercent() != null) {
            discount = subtotal.multiply(coupon.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (type == CouponType.AMOUNT && coupon.getDiscountAmount() != null) {
            discount = coupon.getDiscountAmount();
        } else if (type == CouponType.GIFT && coupon.getGiftAmount() != null) {
            discount = coupon.getGiftAmount();
        }

        if (discount.compareTo(subtotal) > 0) discount = subtotal;
        BigDecimal newTotal = subtotal.subtract(discount).max(BigDecimal.ZERO);

        order.setSubtotalTl(subtotal);
        order.setDiscountAmountTl(discount);
        order.setCouponCode(coupon.getCode().toUpperCase());
        order.setTotalTl(newTotal);
        order = orderRepo.save(order);

        // Kullanım sayısını artır
        coupon.setCurrentUsage((coupon.getCurrentUsage() == null ? 0 : coupon.getCurrentUsage()) + 1);
        couponRepo.save(coupon);

        log.info("Kupon uygulandı: {} → sipariş {} — indirim ₺{}", coupon.getCode(), orderNumber, discount);
        return toResponse(order);
    }

    @Transactional
    public CatalogOrderResponse removeCoupon(String orderNumber) {
        CatalogOrder order = orderRepo.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));

        if (order.getPaymentStatus() == CatalogPaymentStatus.PAID)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ödenmiş siparişten kupon kaldırılamaz");

        if (order.getCouponCode() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Siparişte kupon yok");

        // Kullanım sayısını geri al
        couponRepo.findByCodeIgnoreCase(order.getCouponCode()).ifPresent(c -> {
            if (c.getCurrentUsage() != null && c.getCurrentUsage() > 0)
                c.setCurrentUsage(c.getCurrentUsage() - 1);
            couponRepo.save(c);
        });

        BigDecimal original = order.getSubtotalTl() != null ? order.getSubtotalTl() : order.getTotalTl();
        order.setTotalTl(original);
        order.setDiscountAmountTl(BigDecimal.ZERO);
        order.setCouponCode(null);
        order = orderRepo.save(order);

        return toResponse(order);
    }

    // ─────────── READ ───────────

    @Transactional(readOnly = true)
    public CatalogOrderResponse getById(UUID id) {
        CatalogOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public CatalogOrderResponse getByNumber(String orderNumber) {
        CatalogOrder order = orderRepo.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<CatalogOrderResponse> listAll(String statusFilter) {
        List<CatalogOrder> orders;
        if (statusFilter != null && !statusFilter.isBlank()) {
            try {
                CatalogOrderStatus st = CatalogOrderStatus.valueOf(statusFilter.toUpperCase());
                orders = orderRepo.findByStatusOrderByCreatedAtDesc(st);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz status: " + statusFilter);
            }
        } else {
            orders = orderRepo.findAllByOrderByCreatedAtDesc();
        }
        return orders.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogOrderResponse> listByUser(UUID userId) {
        return orderRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public CatalogOrderResponse updateStatus(UUID id, String newStatus) {
        CatalogOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        CatalogOrderStatus status;
        try {
            status = CatalogOrderStatus.valueOf(newStatus.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz status: " + newStatus);
        }
        order.setStatus(status);
        order = orderRepo.save(order);
        CatalogOrderResponse response = toResponse(order);
        emailService.sendStatusUpdate(response, newStatus);
        return response;
    }

    @Transactional
    public CatalogOrderResponse markShipped(UUID id, String trackingNumber, String cargoCompany) {
        CatalogOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        order.setTrackingNumber(trackingNumber);
        order.setCargoCompany(cargoCompany);
        order.setStatus(CatalogOrderStatus.SHIPPED);
        order = orderRepo.save(order);
        CatalogOrderResponse response = toResponse(order);
        emailService.sendShipped(response, trackingNumber, cargoCompany);
        return response;
    }

    // ─────────── helpers ───────────

    private static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(PASSWORD_CHARS[RAND.nextInt(PASSWORD_CHARS.length)]);
        return sb.toString();
    }

    private String generateOrderNumber() {
        return "CAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal getCurrentKur() {
        return new BigDecimal("45");
    }

    private String buildAttributesSnapshot(Object raw) {
        if (!(raw instanceof List<?> list) || list.isEmpty()) return null;
        List<String> parts = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?,?> entry)) continue;
            UUID attrId = parseUuid(entry.get("attributeId"));
            UUID optId  = parseUuid(entry.get("optionId"));
            if (attrId == null || optId == null) continue;
            CatalogAttribute attr = attrRepo.findById(attrId).orElse(null);
            CatalogAttributeOption opt = optionRepo.findById(optId).orElse(null);
            if (attr == null || opt == null) continue;
            parts.add(attr.getLabel() + ": " + opt.getValue());
        }
        return parts.isEmpty() ? null : String.join("; ", parts);
    }

    private CatalogOrderResponse toResponse(CatalogOrder o) {
        List<CatalogOrderItemResponse> items = itemRepo.findByOrderId(o.getId()).stream()
                .map(i -> CatalogOrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productSlug(i.getProductSlug())
                        .productName(i.getProductName())
                        .mainImageUrl(i.getMainImageUrl())
                        .categoryName(i.getCategoryName())
                        .categorySlug(i.getCategorySlug())
                        .tierQty(i.getTierQty())
                        .priceUsd(i.getPriceUsd())
                        .priceTl(i.getPriceTl())
                        .attributesSnapshot(i.getAttributesSnapshot())
                        .build())
                .toList();

        return CatalogOrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .customerName(o.getCustomerName())
                .customerPhone(o.getCustomerPhone())
                .customerEmail(o.getCustomerEmail())
                .customerAddress(o.getCustomerAddress())
                .city(o.getCity())
                .district(o.getDistrict())
                .notes(o.getNotes())
                .subtotalUsd(o.getSubtotalUsd())
                .subtotalTl(o.getSubtotalTl())
                .discountAmountTl(o.getDiscountAmountTl())
                .couponCode(o.getCouponCode())
                .totalTl(o.getTotalTl())
                .usdKurAtOrder(o.getUsdKurAtOrder())
                .status(o.getStatus().name())
                .paymentStatus(o.getPaymentStatus() != null ? o.getPaymentStatus().name() : null)
                .userId(o.getUserId())
                .items(items)
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .trackingNumber(o.getTrackingNumber())
                .cargoCompany(o.getCargoCompany())
                .build();
    }

    private static String required(Map<String,Object> body, String key, String errMsg) {
        Object v = body.get(key);
        if (v == null || v.toString().trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errMsg);
        return v.toString().trim();
    }

    private static String asString(Object v) {
        if (v == null) return null;
        String s = v.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private static UUID parseUuid(Object v) {
        if (v == null) return null;
        try { return UUID.fromString(v.toString().trim()); }
        catch (Exception e) { return null; }
    }

    private static BigDecimal parseDecimal(Object v) {
        if (v == null) return null;
        try { return new BigDecimal(v.toString().trim()).setScale(2, RoundingMode.HALF_UP); }
        catch (Exception e) { return null; }
    }
}