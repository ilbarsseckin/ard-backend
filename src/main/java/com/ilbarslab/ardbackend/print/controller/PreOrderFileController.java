package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.PreOrderFileResponse;
import com.ilbarslab.ardbackend.print.service.PreOrderFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/pre-order-files")
@RequiredArgsConstructor
@Slf4j
public class PreOrderFileController {

    private final PreOrderFileService service;

    /** Ürün detay sayfasından dosya yükleme (orderNumber gerekmez). */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<PreOrderFileResponse>> upload(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            PreOrderFileResponse res = service.upload(file);
            return ResponseEntity.ok(ApiResponse.ok(res));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Dosya yüklenemedi"));
        }
    }

    /** Sepete eklenmeden önce dosyayı silme. */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IOException e) {
            log.error("File delete failed", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Dosya silinemedi"));
        }
    }

    /** Bir siparişe bağlı tüm tasarım dosyalarını getir (odeme-katalog read-only). */
    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<ApiResponse<List<PreOrderFileResponse>>> getByOrder(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(service.getByOrder(orderId)));
    }
}