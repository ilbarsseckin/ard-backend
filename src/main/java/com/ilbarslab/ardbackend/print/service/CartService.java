package com.ilbarslab.ardbackend.print.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilbarslab.ardbackend.print.dto.request.AddCartItemRequest;
import com.ilbarslab.ardbackend.print.dto.request.PriceCalculateRequest;
import com.ilbarslab.ardbackend.print.dto.response.CartItemResponse;
import com.ilbarslab.ardbackend.print.dto.response.CartResponse;
import com.ilbarslab.ardbackend.print.dto.response.PriceCalculateResponse;
import com.ilbarslab.ardbackend.print.entity.*;
import com.ilbarslab.ardbackend.print.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
    private final ObjectMapper objectMapper;

    @Transactional
    public CartResponse getCart(String email) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        User user = getUser(email);
        Cart cart = getOrCreateCart(user);

        ProductType product = productTypeRepository.findBySlug(request.getProductSlug())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + request.getProductSlug()));

        // Fiyat hesapla
        PriceCalculateRequest priceReq = new PriceCalculateRequest();
        priceReq.setProductSlug(request.getProductSlug());
        priceReq.setWidthCm(request.getWidthCm());
        priceReq.setHeightCm(request.getHeightCm());
        priceReq.setQuantity(request.getQuantity());
        priceReq.setOptions(request.getOptions());

        PriceCalculateResponse price = pricingService.calculate(priceReq);

        // Options JSON
        String optionsJson = null;
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            try {
                optionsJson = objectMapper.writeValueAsString(request.getOptions());
            } catch (Exception e) {
                log.warn("Options JSON dönüştürme hatası", e);
            }
        }

        CartItem item = CartItem.builder()
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
        cart.setExpiresAt(LocalDateTime.now().plusHours(24));
        cartRepository.save(cart);

        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse attachFile(String email, UUID itemId,
                                   String s3Key, String originalName, int pageCount) {
        User user = getUser(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sepet kalemi bulunamadı"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu işlem için yetkiniz yok");
        }

        item.setFileS3Key(s3Key);
        item.setFileOriginalName(originalName);
        item.setFilePagesCount(pageCount);
        cartItemRepository.save(item);

        return toCartResponse(item.getCart());
    }

    @Transactional
    public CartResponse removeItem(String email, UUID itemId) {
        User user = getUser(email);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Sepet kalemi bulunamadı"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bu işlem için yetkiniz yok");
        }

        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        cartRepository.save(cart);

        return toCartResponse(cart);
    }

    @Transactional
    public void clearCart(String email) {
        User user = getUser(email);
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
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
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    private CartResponse toCartResponse(Cart cart) {
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
