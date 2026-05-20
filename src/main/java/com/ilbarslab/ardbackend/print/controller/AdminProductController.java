package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.BulkPriceUpdateRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.ImportResultResponse;
import com.ilbarslab.ardbackend.print.dto.response.ProductTypeResponse;
import com.ilbarslab.ardbackend.print.service.ProductImportService;
import com.ilbarslab.ardbackend.print.service.ProductService;
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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAll()));
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
