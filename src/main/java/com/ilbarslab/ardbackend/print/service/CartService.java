package com.ilbarslab.ardbackend.print.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilbarslab.ardbackend.print.dto.request.AddCartItemRequest;
import com.ilbarslab.ardbackend.print.dto.request.PriceCalculateRequest;
import com.ilbarslab.ardbackend.print.dto.response.CartItemResponse;
import com.ilbarslab.ardbackend.print.dto.response.CartResponse;
import com.ilbarslab.ardbackend.print.dto.response.PriceCalculateResponse;
import com.ilbarslab.ardbackend.print.entity.*;
import com.ilbarslab.ardbackend.print.entity.enums.DealerStatus;
import com.ilbarslab.ardbackend.print.entity.enums.Role;
import com.ilbarslab.ardbackend.print.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductTypeRepository productTypeRepository;
    private final PricingService pricingService;
    private final DealerRepository dealerRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public CartResponse getCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        return toCartResponse(cart, null);
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        ProductType product = productTypeRepository.findBySlug(request.getProductSlug())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + request.getProductSlug()));

        // Options'ı JSON'a çevir — merge karşılaştırması için aynı string'i kullanacağız
        String optionsJson = serializeOptions(request);

        // Aynı kombinasyonda + dosyası yüklenmemiş bir satır var mı?
        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getProductType().getId().equals(product.getId())
                        && Objects.equals(ci.getWidthCm(), request.getWidthCm())
                        && Objects.equals(ci.getHeightCm(), request.getHeightCm())
                        && Objects.equals(ci.getOptionsJson(), optionsJson)
                        && ci.getFileS3Key() == null)
                .findFirst()
                .orElse(null);

        CartItem item;
        if (existing != null) {
            // ── BİRLEŞTİR: miktarı arttır, fiyatı yeniden hesapla (barem değişebilir!)
            int oldQty = existing.getQuantity();
            int newQty = oldQty + request.getQuantity();
            PriceCalculateResponse price = calculateWithDealerDiscount(user, request, newQty);

            existing.setQuantity(newQty);
            existing.setUnitPrice(price.getUnitPrice());
            existing.setTotalPrice(price.getTotalPrice());
            existing.setPriceBreakdown(price.getPriceBreakdown());
            existing.setDeclaredPrints(request.getDeclaredPrints());
            item = existing;

            log.info("Sepet satırı birleştirildi: itemId={} eskiQty={} yeniQty={}",
                    existing.getId(), oldQty, newQty);
        } else {
            // ── YENİ SATIR
            PriceCalculateResponse price = calculateWithDealerDiscount(
                    user, request, request.getQuantity());

            item = CartItem.builder()
                    .cart(cart)
                    .productType(product)
                    .widthCm(request.getWidthCm())
                    .heightCm(request.getHeightCm())
                    .quantity(request.getQuantity())
                    .unitPrice(price.getUnitPrice())
                    .totalPrice(price.getTotalPrice())
                    .optionsJson(optionsJson)
                    .declaredPrints(request.getDeclaredPrints())
                    .priceBreakdown(price.getPriceBreakdown())
                    .build();
            cart.getItems().add(item);
        }

        cart.setExpiresAt(LocalDateTime.now().plusHours(24));
        cartRepository.save(cart);

        // id garantilemek için flush
        if (item.getId() == null) {
            item = cartItemRepository.saveAndFlush(item);
        }

        return toCartResponse(cart, item.getId());
    }

    @Transactional
    public CartResponse attachFile(String email, UUID itemId,
                                   String s3Key, String originalName, int pageCount) {
        User user = getUser(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sepet kalemi bulunamadı"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz yok");
        }

        item.setFileS3Key(s3Key);
        item.setFileOriginalName(originalName);
        item.setFilePagesCount(pageCount);
        cartItemRepository.save(item);

        return toCartResponse(item.getCart(), null);
    }

    @Transactional
    public CartResponse removeItem(String email, UUID itemId) {
        User user = getUser(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sepet kalemi bulunamadı"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu işlem için yetkiniz yok");
        }

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);

        return toCartResponse(cart, null);
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    // ───────────── private helpers ─────────────

    private PriceCalculateResponse calculateWithDealerDiscount(
            User user, AddCartItemRequest request, int quantity) {

        PriceCalculateRequest priceReq = new PriceCalculateRequest();
        priceReq.setProductSlug(request.getProductSlug());
        priceReq.setWidthCm(request.getWidthCm());
        priceReq.setHeightCm(request.getHeightCm());
        priceReq.setQuantity(quantity);
        priceReq.setOptions(request.getOptions());

        PriceCalculateResponse price = pricingService.calculate(priceReq);

        if (user.getRole() == Role.DEALER) {
            dealerRepository.findByUserId(user.getId()).ifPresent(dealer -> {
                if (dealer.getStatus() == DealerStatus.APPROVED
                        && dealer.getDiscountRate() != null
                        && dealer.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {

                    BigDecimal rate = dealer.getDiscountRate()
                            .divide(BigDecimal.valueOf(100));
                    BigDecimal multiplier = BigDecimal.ONE.subtract(rate);

                    price.setUnitPrice(price.getUnitPrice()
                            .multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
                    price.setTotalPrice(price.getTotalPrice()
                            .multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
                    price.setPriceBreakdown(
                            price.getPriceBreakdown()
                                    + " (%" + dealer.getDiscountRate()
                                    .stripTrailingZeros().toPlainString()
                                    + " bayi indirimi)"
                    );

                    log.info("Bayi iskontosu uygulandı: kullanıcı={} oran=%{}",
                            user.getEmail(), dealer.getDiscountRate());
                }
            });
        }
        return price;
    }

    private String serializeOptions(AddCartItemRequest request) {
        if (request.getOptions() == null || request.getOptions().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request.getOptions());
        } catch (Exception e) {
            log.warn("Options JSON dönüştürme hatası", e);
            return null;
        }
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusHours(24))
                        .build()));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
    }

    private CartResponse toCartResponse(Cart cart, UUID addedItemId) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasWarnings = itemResponses.stream()
                .anyMatch(CartItemResponse::isPageWarning);

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .subtotal(subtotal)
                .totalItems(itemResponses.size())
                .hasWarnings(hasWarnings)
                .addedItemId(addedItemId)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        boolean pageWarning = item.getFilePagesCount() != null &&
                item.getFilePagesCount() > item.getDeclaredPrints();

        return CartItemResponse.builder()
                .id(item.getId())
                .productName(item.getProductType().getName())
                .productSlug(item.getProductType().getSlug())
                .widthCm(item.getWidthCm())
                .heightCm(item.getHeightCm())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .priceBreakdown(item.getPriceBreakdown())
                .fileOriginalName(item.getFileOriginalName())
                .filePagesCount(item.getFilePagesCount())
                .declaredPrints(item.getDeclaredPrints())
                .hasFile(item.getFileS3Key() != null)
                .pageWarning(pageWarning)
                .build();
    }
}