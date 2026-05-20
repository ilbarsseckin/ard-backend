package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.AddCartItemRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.CartResponse;
import com.ilbarslab.ardbackend.print.dto.response.FileUploadResponse;
import com.ilbarslab.ardbackend.print.service.CartService;
import com.ilbarslab.ardbackend.print.service.PdfService;
import com.ilbarslab.ardbackend.print.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final PdfService pdfService;
    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        CartResponse cart = cartService.getCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddCartItemRequest request) {
        CartResponse cart = cartService.addItem(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok("Ürün sepete eklendi", cart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID itemId) {
        CartResponse cart = cartService.removeItem(userDetails.getUsername(), itemId);
        return ResponseEntity.ok(ApiResponse.ok("Ürün sepetten çıkarıldı", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Sepet temizlendi", null));
    }

    // PDF yükle ve sepet kalemine bağla
    @PostMapping("/items/{itemId}/file")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID itemId,
            @RequestParam("file") MultipartFile file) {
        try {
            // PDF sayfa sayısını oku
            int pageCount = pdfService.countPages(file);

            // R2'ye yükle — kilitli klasör
            String s3Key = storageService.uploadFile(file, "orders/locked");

            // Sepet kalemine bağla
            CartResponse cart = cartService.attachFile(
                    userDetails.getUsername(), itemId,
                    s3Key, file.getOriginalFilename(), pageCount);

            // Uyarı mesajı üret
            var cartItem = cart.getItems().stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst();

            boolean pageWarning = cartItem.map(i -> i.getFilePagesCount() > i.getDeclaredPrints()).orElse(false);
            String warningMsg = null;
            if (pageWarning && cartItem.isPresent()) {
                warningMsg = pdfService.buildWarningMessage(
                        cartItem.get().getFilePagesCount(),
                        cartItem.get().getDeclaredPrints());
            }

            FileUploadResponse response = FileUploadResponse.builder()
                    .s3Key(s3Key)
                    .originalName(file.getOriginalFilename())
                    .pageCount(pageCount)
                    .pageWarning(pageWarning)
                    .warningMessage(warningMsg)
                    .build();

            return ResponseEntity.ok(ApiResponse.ok(
                    pageWarning ? "Dosya yüklendi — sayfa uyarısı mevcut" : "Dosya başarıyla yüklendi",
                    response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Dosya yükleme hatası: " + e.getMessage()));
        }
    }
}
