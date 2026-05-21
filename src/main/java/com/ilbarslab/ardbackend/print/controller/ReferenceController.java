package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.ReferenceRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.ReferenceResponse;
import com.ilbarslab.ardbackend.print.service.ReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/references")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReferenceResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(referenceService.getAll()));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ReferenceResponse>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.ok(referenceService.getByCategory(category)));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<ReferenceResponse>>> getFeatured() {
        return ResponseEntity.ok(ApiResponse.ok(referenceService.getFeatured()));
    }

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public ResponseEntity<ApiResponse<ReferenceResponse>> create(
            @RequestParam("name")     String name,
            @RequestParam("sector")   String sector,
            @RequestParam("category") String category,
            @RequestParam(value = "description",  required = false) String description,
            @RequestParam(value = "color",        required = false) String color,
            @RequestParam(value = "abbr",         required = false) String abbr,
            @RequestParam(value = "featured",     required = false) String featured,
            @RequestParam(value = "active",       required = false) String active,
            @RequestParam(value = "displayOrder", required = false) String displayOrder,
            @RequestParam(value = "logo",         required = false) MultipartFile logo) throws IOException {

        return ResponseEntity.ok(ApiResponse.ok("Referans eklendi",
                referenceService.create(buildRequest(name, sector, category, description, color, abbr, featured, active, displayOrder), logo)));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public ResponseEntity<ApiResponse<ReferenceResponse>> update(
            @PathVariable UUID id,
            @RequestParam("name")     String name,
            @RequestParam("sector")   String sector,
            @RequestParam("category") String category,
            @RequestParam(value = "description",  required = false) String description,
            @RequestParam(value = "color",        required = false) String color,
            @RequestParam(value = "abbr",         required = false) String abbr,
            @RequestParam(value = "featured",     required = false) String featured,
            @RequestParam(value = "active",       required = false) String active,
            @RequestParam(value = "displayOrder", required = false) String displayOrder,
            @RequestParam(value = "logo",         required = false) MultipartFile logo) throws IOException {

        return ResponseEntity.ok(ApiResponse.ok("Referans güncellendi",
                referenceService.update(id, buildRequest(name, sector, category, description, color, abbr, featured, active, displayOrder), logo)));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public ResponseEntity<ApiResponse<ReferenceResponse>> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(referenceService.toggleActive(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        referenceService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Referans silindi", null));
    }

    private ReferenceRequest buildRequest(String name, String sector, String category,
                                          String description, String color, String abbr,
                                          String featured, String active, String displayOrder) {
        ReferenceRequest req = new ReferenceRequest();
        req.setName(name);
        req.setSector(sector);
        req.setCategory(category);
        req.setDescription(description);
        req.setColor(color != null ? color : "#F4821F");
        req.setAbbr(abbr);
        req.setFeatured("true".equalsIgnoreCase(featured));
        req.setActive(!"false".equalsIgnoreCase(active));
        req.setDisplayOrder(displayOrder != null ? Integer.parseInt(displayOrder) : 0);
        return req;
    }
}