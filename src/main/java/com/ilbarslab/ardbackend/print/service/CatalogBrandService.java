package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.CatalogBrandResponse;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogBrand;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogBrandRepository;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogBrandService {

    private final CatalogBrandRepository brandRepo;
    private final CatalogProductRepository productRepo;

    // ─────────── READ ───────────

    public List<CatalogBrandResponse> getAll() {
        return brandRepo.findAll().stream()
            .sorted(Comparator.comparing(CatalogBrand::getName))
            .map(this::toResponse)
            .toList();
    }

    public List<CatalogBrandResponse> getActive() {
        return brandRepo.findByActiveTrueOrderByNameAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    public CatalogBrandResponse getById(UUID id) {
        return toResponse(brandRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marka bulunamadı")));
    }

    public CatalogBrandResponse getBySlug(String slug) {
        return toResponse(brandRepo.findBySlug(slug)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marka bulunamadı: " + slug)));
    }

    // ─────────── CREATE ───────────

    @Transactional
    public CatalogBrandResponse create(Map<String, Object> body) {
        String slug = asString(body.get("slug"));
        String name = asString(body.get("name"));

        if (slug == null || slug.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug zorunlu");
        if (name == null || name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "İsim zorunlu");
        validateSlugFormat(slug);

        if (brandRepo.findBySlug(slug).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu slug zaten kullanılıyor: " + slug);

        CatalogBrand brand = CatalogBrand.builder()
            .slug(slug)
            .name(name)
            .logoUrl(asString(body.get("logoUrl")))
            .description(asString(body.get("description")))
            .active(asBoolean(body.get("active"), true))
            .build();

        brand = brandRepo.save(brand);
        log.info("Marka oluşturuldu: {} ({})", brand.getName(), brand.getSlug());
        return toResponse(brand);
    }

    // ─────────── UPDATE ───────────

    @Transactional
    public CatalogBrandResponse update(UUID id, Map<String, Object> body) {
        CatalogBrand brand = brandRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marka bulunamadı"));

        if (body.containsKey("slug")) {
            String newSlug = asString(body.get("slug"));
            if (newSlug != null && !newSlug.equals(brand.getSlug())) {
                validateSlugFormat(newSlug);
                if (brandRepo.findBySlug(newSlug).isPresent())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu slug zaten kullanılıyor: " + newSlug);
                brand.setSlug(newSlug);
            }
        }

        if (body.containsKey("name"))        brand.setName(asString(body.get("name")));
        if (body.containsKey("logoUrl"))     brand.setLogoUrl(asString(body.get("logoUrl")));
        if (body.containsKey("description")) brand.setDescription(asString(body.get("description")));
        if (body.containsKey("active"))      brand.setActive(asBoolean(body.get("active"), brand.getActive()));

        brand = brandRepo.save(brand);
        return toResponse(brand);
    }

    // ─────────── DELETE ───────────

    @Transactional
    public void delete(UUID id) {
        CatalogBrand brand = brandRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marka bulunamadı"));

        long productCount = productRepo.findByBrandIdOrderBySortOrderAsc(id).size();
        if (productCount > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Bu markayı " + productCount + " ürün kullanıyor, önce onları başka markaya taşı veya markayı kaldır");
        }

        brandRepo.delete(brand);
        log.info("Marka silindi: {}", brand.getName());
    }

    // ─────────── TOGGLE ───────────

    @Transactional
    public CatalogBrandResponse toggleActive(UUID id) {
        CatalogBrand brand = brandRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Marka bulunamadı"));
        brand.setActive(!Boolean.TRUE.equals(brand.getActive()));
        brand = brandRepo.save(brand);
        return toResponse(brand);
    }

    // ─────────── private helpers ───────────

    private void validateSlugFormat(String slug) {
        if (!slug.matches("[a-z0-9-]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Slug sadece küçük harf, rakam ve tire içerebilir");
        }
    }

    private CatalogBrandResponse toResponse(CatalogBrand b) {
        long productCount = productRepo.findByBrandIdOrderBySortOrderAsc(b.getId()).size();
        return CatalogBrandResponse.builder()
            .id(b.getId())
            .slug(b.getSlug())
            .name(b.getName())
            .logoUrl(b.getLogoUrl())
            .description(b.getDescription())
            .active(b.getActive())
            .productCount(productCount)
            .createdAt(b.getCreatedAt())
            .build();
    }

    private static String asString(Object v) {
        return v == null ? null : v.toString();
    }

    private static Boolean asBoolean(Object v, Boolean fallback) {
        if (v == null) return fallback;
        String s = v.toString().trim().toLowerCase();
        return s.equals("true") || s.equals("1") || s.equals("on");
    }
}
