package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService storageService;

    /**
     * Dosya yükle.
     * Form-data: file=<dosya>, type=<brand|product|category|general>
     * Dönüş: { data: { url }, message }
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "general") String type) {

        String url = storageService.storeFile(file, type);

        return ResponseEntity.ok(Map.of(
            "data", Map.of("url", url),
            "message", "Yüklendi"
        ));
    }

    /** URL ile dosya silme (opsiyonel kullanım) */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam String url) {
        storageService.deleteByUrl(url);
        return ResponseEntity.ok(Map.of("message", "Silindi (varsa)"));
    }
}
