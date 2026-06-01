package com.ilbarslab.ardbackend.print.service;


import com.ilbarslab.ardbackend.print.dto.response.CatalogAttributeOptionResponse;
import com.ilbarslab.ardbackend.print.dto.response.CatalogAttributeResponse;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogAttribute;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogAttributeOption;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogCategory;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogAttributeOptionRepository;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogAttributeRepository;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogCategoryRepository;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogProductAttributeValueRepository;
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
public class CatalogAttributeService {

    private final CatalogAttributeRepository attributeRepo;
    private final CatalogAttributeOptionRepository optionRepo;
    private final CatalogCategoryRepository categoryRepo;
    private final CatalogProductAttributeValueRepository pavRepo;

    private static final Set<String> VALID_INPUT_TYPES = Set.of("select", "text", "color", "image");

    // ─────────── ATTRIBUTE CRUD ───────────

    public List<CatalogAttributeResponse> listByCategory(UUID categoryId) {
        if (!categoryRepo.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı");
        }
        return attributeRepo.findByCategoryIdOrderBySortOrderAsc(categoryId).stream()
            .map(this::toAttributeResponse)
            .toList();
    }

    @Transactional
    public CatalogAttributeResponse createAttribute(UUID categoryId, Map<String, Object> body) {
        CatalogCategory cat = categoryRepo.findById(categoryId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı"));

        String attrKey = asString(body.get("attrKey"));
        String label = asString(body.get("label"));

        if (attrKey == null || attrKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Öznitelik anahtarı (attrKey) zorunlu");
        }
        if (label == null || label.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Etiket (label) zorunlu");
        }
        validateAttrKey(attrKey);

        // Aynı kategoride aynı key var mı?
        boolean duplicate = attributeRepo.findByCategoryIdOrderBySortOrderAsc(categoryId).stream()
            .anyMatch(a -> a.getAttrKey().equals(attrKey));
        if (duplicate) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Bu kategoride aynı anahtarla bir öznitelik zaten var: " + attrKey);
        }

        String inputType = asString(body.getOrDefault("inputType", "select"));
        if (!VALID_INPUT_TYPES.contains(inputType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Geçersiz inputType. Geçerli olanlar: " + VALID_INPUT_TYPES);
        }

        CatalogAttribute attr = CatalogAttribute.builder()
            .category(cat)
            .attrKey(attrKey)
            .label(label)
            .inputType(inputType)
            .required(asBoolean(body.get("required"), false))
            .sortOrder(asInt(body.get("sortOrder"), 0))
            .build();

        attr = attributeRepo.save(attr);
        log.info("Öznitelik oluşturuldu: {} ({}) kategori={}", label, attrKey, cat.getName());
        return toAttributeResponse(attr);
    }

    @Transactional
    public CatalogAttributeResponse updateAttribute(UUID id, Map<String, Object> body) {
        CatalogAttribute attr = attributeRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Öznitelik bulunamadı"));

        if (body.containsKey("attrKey")) {
            String newKey = asString(body.get("attrKey"));
            if (newKey != null && !newKey.equals(attr.getAttrKey())) {
                validateAttrKey(newKey);
                boolean duplicate = attributeRepo.findByCategoryIdOrderBySortOrderAsc(attr.getCategory().getId()).stream()
                    .anyMatch(a -> !a.getId().equals(id) && a.getAttrKey().equals(newKey));
                if (duplicate) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Bu kategoride aynı anahtarla bir öznitelik zaten var: " + newKey);
                }
                attr.setAttrKey(newKey);
            }
        }
        if (body.containsKey("label"))     attr.setLabel(asString(body.get("label")));
        if (body.containsKey("inputType")) {
            String it = asString(body.get("inputType"));
            if (it != null && !VALID_INPUT_TYPES.contains(it)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz inputType");
            }
            attr.setInputType(it);
        }
        if (body.containsKey("required"))  attr.setRequired(asBoolean(body.get("required"), attr.getRequired()));
        if (body.containsKey("sortOrder")) attr.setSortOrder(asInt(body.get("sortOrder"), attr.getSortOrder()));

        attr = attributeRepo.save(attr);
        return toAttributeResponse(attr);
    }

    @Transactional
    public void deleteAttribute(UUID id) {
        CatalogAttribute attr = attributeRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Öznitelik bulunamadı"));

        // Bir ürün tarafından kullanılıyorsa silinemez
        long usageCount = pavRepo.countByAttributeId(id);
        if (usageCount > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Bu öznitelik " + usageCount + " ürün tarafından kullanılıyor, silinemez");
        }

        // Seçenekleri de sil
        optionRepo.deleteByAttributeId(id);
        attributeRepo.delete(attr);
        log.info("Öznitelik silindi: {} ({})", attr.getLabel(), attr.getAttrKey());
    }

    // ─────────── OPTION CRUD ───────────

    @Transactional
    public CatalogAttributeOptionResponse createOption(UUID attributeId, Map<String, Object> body) {
        CatalogAttribute attr = attributeRepo.findById(attributeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Öznitelik bulunamadı"));

        String value = asString(body.get("value"));
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Değer (value) zorunlu");
        }

        CatalogAttributeOption opt = CatalogAttributeOption.builder()
            .attribute(attr)
            .value(value)
            .colorHex(asString(body.get("colorHex")))
            .sortOrder(asInt(body.get("sortOrder"), 0))
            .build();

        opt = optionRepo.save(opt);
        return toOptionResponse(opt);
    }

    @Transactional
    public CatalogAttributeOptionResponse updateOption(UUID id, Map<String, Object> body) {
        CatalogAttributeOption opt = optionRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seçenek bulunamadı"));

        if (body.containsKey("value"))     opt.setValue(asString(body.get("value")));
        if (body.containsKey("colorHex"))  opt.setColorHex(asString(body.get("colorHex")));
        if (body.containsKey("sortOrder")) opt.setSortOrder(asInt(body.get("sortOrder"), opt.getSortOrder()));

        opt = optionRepo.save(opt);
        return toOptionResponse(opt);
    }

    @Transactional
    public void deleteOption(UUID id) {
        CatalogAttributeOption opt = optionRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seçenek bulunamadı"));

        long usageCount = pavRepo.countByOptionId(id);
        if (usageCount > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Bu seçenek " + usageCount + " ürün tarafından kullanılıyor, silinemez");
        }

        optionRepo.delete(opt);
    }

    // ─────────── private helpers ───────────

    private void validateAttrKey(String key) {
        if (!key.matches("[a-z][a-z0-9_]*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Anahtar küçük harfle başlamalı, sadece küçük harf, rakam ve alt çizgi içerebilir (örn: kartus_rengi)");
        }
    }

    private CatalogAttributeResponse toAttributeResponse(CatalogAttribute a) {
        List<CatalogAttributeOptionResponse> options = optionRepo
            .findByAttributeIdOrderBySortOrderAsc(a.getId()).stream()
            .map(this::toOptionResponse)
            .toList();

        return CatalogAttributeResponse.builder()
            .id(a.getId())
            .categoryId(a.getCategory().getId())
            .attrKey(a.getAttrKey())
            .label(a.getLabel())
            .inputType(a.getInputType())
            .required(a.getRequired())
            .sortOrder(a.getSortOrder())
            .options(options)
            .build();
    }

    private CatalogAttributeOptionResponse toOptionResponse(CatalogAttributeOption o) {
        return CatalogAttributeOptionResponse.builder()
            .id(o.getId())
            .attributeId(o.getAttribute().getId())
            .value(o.getValue())
            .colorHex(o.getColorHex())
            .sortOrder(o.getSortOrder())
            .build();
    }

    private static String asString(Object v) {
        return v == null ? null : v.toString();
    }

    private static Integer asInt(Object v, Integer fallback) {
        if (v == null) return fallback;
        try { return Integer.parseInt(v.toString().trim()); }
        catch (Exception e) { return fallback; }
    }

    private static Boolean asBoolean(Object v, Boolean fallback) {
        if (v == null) return fallback;
        String s = v.toString().trim().toLowerCase();
        return s.equals("true") || s.equals("1") || s.equals("on");
    }
}
