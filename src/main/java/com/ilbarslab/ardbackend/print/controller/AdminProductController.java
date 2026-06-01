package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.BulkPriceUpdateRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.ImportResultResponse;
import com.ilbarslab.ardbackend.print.dto.response.ProductTypeResponse;
import com.ilbarslab.ardbackend.print.service.ProductImportService;
import com.ilbarslab.ardbackend.print.service.ProductService;
import com.ilbarslab.ardbackend.print.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
public class AdminProductController {

    private final ProductService productService;
    private final ProductImportService productImportService;
    private final StorageService storageService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductTypeResponse>> create(
            @RequestBody Map<String, Object> body) {
        ProductTypeResponse created = productService.create(body);
        return ResponseEntity.ok(ApiResponse.ok("Ürün eklendi", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> update(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        ProductTypeResponse updated = productService.update(id, body);
        return ResponseEntity.ok(ApiResponse.ok("Ürün güncellendi", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Ürün silindi", null));
    }

    // Ürün resmi yükle
    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = storageService.uploadProductImage(id.toString(), file);
            productService.updateImageUrl(id, imageUrl);
            return ResponseEntity.ok(ApiResponse.ok("Resim yüklendi", imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Resim yüklenemedi: " + e.getMessage()));
        }
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<ImportResultResponse>> importProducts(
            @RequestParam("file") MultipartFile file) {
        try {
            ImportResultResponse result = productImportService.importFromFile(file);
            String message = String.format("%d ürün eklendi, %d güncellendi, %d hata",
                    result.getImported(), result.getUpdated(), result.getErrors());
            return ResponseEntity.ok(ApiResponse.ok(message, result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Import hatası: " + e.getMessage()));
        }
    }

    @PatchMapping("/bulk-price")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> bulkUpdatePrice(
            @Valid @RequestBody BulkPriceUpdateRequest request) {
        int updated = productImportService.bulkUpdatePrice(request);
        return ResponseEntity.ok(ApiResponse.ok("Fiyatlar güncellendi",
                Map.of("updatedRules", updated)));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleActive(@PathVariable UUID id) {
        productService.toggleActive(id);
        return ResponseEntity.ok(ApiResponse.ok("Ürün durumu güncellendi", null));
    }
}