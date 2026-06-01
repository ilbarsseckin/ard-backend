package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.PriceTierDto;
import com.ilbarslab.ardbackend.print.dto.response.ProductTypeResponse;
import com.ilbarslab.ardbackend.print.entity.PriceRule;
import com.ilbarslab.ardbackend.print.entity.ProductType;
import com.ilbarslab.ardbackend.print.repository.PriceRuleRepository;
import com.ilbarslab.ardbackend.print.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductTypeRepository productTypeRepository;
    private final PriceRuleRepository priceRuleRepository;

    public List<ProductTypeResponse> getAllActive() {
        return productTypeRepository.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    public List<ProductTypeResponse> getAll() {
        return productTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductTypeResponse getBySlug(String slug) {
        ProductType p = productTypeRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + slug));
        return toResponse(p);
    }

    public void toggleActive(UUID id) {
        ProductType p = productTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        p.setIsActive(!p.getIsActive());
        productTypeRepository.save(p);
    }

    @Transactional
    public ProductTypeResponse create(Map<String, Object> body) {
        String name = asString(body.get("name"));
        String slug = asString(body.get("slug"));
        if (name == null || name.isBlank() || slug == null || slug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ürün adı ve slug zorunlu");
        }
        if (productTypeRepository.findBySlug(slug).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu slug zaten kullanılıyor: " + slug);
        }

        ProductType p = ProductType.builder()
                .name(name).slug(slug)
                .pricingModel(asString(body.getOrDefault("pricingModel", "UNIT")))
                .unit(asString(body.getOrDefault("unit", "adet")))
                .minOrder(asInt(body.get("minOrder"), 1))
                .description(asString(body.getOrDefault("description", "")))
                .imageUrl(asString(body.get("imageUrl")))
                .isActive(true)
                .featured(asBoolean(body.get("featured"), false))
                .badge(asString(body.get("badge")))
                .originalPrice(asBigDecimal(body.get("originalPrice")))
                .build();
        p = productTypeRepository.save(p);

        applyPricing(p, body);
        return toResponse(p);
    }

    @Transactional
    public ProductTypeResponse update(UUID id, Map<String, Object> body) {
        ProductType p = productTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));

        if (body.containsKey("name"))          p.setName(asString(body.get("name")));
        if (body.containsKey("slug"))          p.setSlug(asString(body.get("slug")));
        if (body.containsKey("pricingModel"))  p.setPricingModel(asString(body.get("pricingModel")));
        if (body.containsKey("unit"))          p.setUnit(asString(body.get("unit")));
        if (body.containsKey("minOrder"))      p.setMinOrder(asInt(body.get("minOrder"), p.getMinOrder()));
        if (body.containsKey("description"))   p.setDescription(asString(body.get("description")));
        if (body.containsKey("imageUrl"))      p.setImageUrl(asString(body.get("imageUrl")));
        if (body.containsKey("featured"))      p.setFeatured(asBoolean(body.get("featured"), false));
        if (body.containsKey("badge"))         p.setBadge(asString(body.get("badge")));
        if (body.containsKey("originalPrice")) p.setOriginalPrice(asBigDecimal(body.get("originalPrice")));
        p = productTypeRepository.save(p);

        // Fiyat bilgisi geldiyse uygula (basePrice veya priceTiers)
        if (body.containsKey("basePrice") || body.containsKey("priceTiers")) {
            applyPricing(p, body);
        }
        return toResponse(p);
    }

    public void delete(UUID id) {
        if (!productTypeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı");
        }
        productTypeRepository.deleteById(id);
    }

    public void updateImageUrl(UUID id, String imageUrl) {
        ProductType p = productTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        p.setImageUrl(imageUrl);
        productTypeRepository.save(p);
    }

    // ───────────── private helpers ─────────────

    /**
     * Fiyat tanımını uygula.
     * - priceTiers varsa: tüm baremleri yaz (kademeli ürünler)
     * - basePrice varsa: tek bir barem yaz (basit ürünler)
     * Önceki option-less kuralları siler, yenilerini yazar.
     */
    @SuppressWarnings("unchecked")
    private void applyPricing(ProductType p, Map<String, Object> body) {
        boolean isArea = "AREA_BASED".equals(p.getPricingModel());

        List<PriceTierDto> tiers = new ArrayList<>();
        Object tiersObj = body.get("priceTiers");
        if (tiersObj instanceof List<?> rawList && !rawList.isEmpty()) {
            for (Object o : rawList) {
                if (!(o instanceof Map<?,?> m)) continue;
                Integer minQty = asInt(m.get("minQty"), null);
                Integer maxQty = asInt(m.get("maxQty"), null);
                BigDecimal price = asBigDecimal(m.get("price"));
                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) continue;
                tiers.add(new PriceTierDto(minQty, maxQty, price));
            }
        } else {
            BigDecimal basePrice = asBigDecimal(body.get("basePrice"));
            if (basePrice != null && basePrice.compareTo(BigDecimal.ZERO) > 0) {
                tiers.add(new PriceTierDto(p.getMinOrder(), null, basePrice));
            }
        }

        if (tiers.isEmpty()) {
            log.warn("Ürün {} için fiyat bilgisi gelmedi", p.getSlug());
            return;
        }

        // Sırala + çakışma kontrolü (kademeli modeller için)
        tiers.sort(Comparator.comparing(t -> t.getMinQty() == null ? 0 : t.getMinQty()));
        boolean needsRanges = "TIERED_QUANTITY".equals(p.getPricingModel())
                || "PACKAGE".equals(p.getPricingModel());
        if (needsRanges && tiers.size() > 1) {
            for (int i = 0; i < tiers.size() - 1; i++) {
                PriceTierDto a = tiers.get(i), b = tiers.get(i + 1);
                if (a.getMaxQty() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Sınırsız (max boş) barem en sonda olmalı");
                }
                if (a.getMaxQty() >= b.getMinQty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Barem aralıkları çakışıyor: " + a.getMaxQty() + " ≥ " + b.getMinQty());
                }
            }
        }

        // Mevcut option-less kuralları sil
        List<PriceRule> existing = priceRuleRepository.findByProductTypeIdOrderByMinQtyAsc(p.getId());
        existing.stream().filter(r -> r.getOptionKey() == null).forEach(priceRuleRepository::delete);
        priceRuleRepository.flush();

        // Yeni baremleri yaz (USD)
        for (PriceTierDto t : tiers) {
            priceRuleRepository.save(PriceRule.builder()
                    .productType(p)
                    .ruleType(p.getPricingModel())
                    .basePrice(isArea ? t.getPrice() : null)
                    .unitPrice(!isArea ? t.getPrice() : null)
                    .minQty(t.getMinQty() != null ? t.getMinQty() : p.getMinOrder())
                    .maxQty(t.getMaxQty())
                    .build());
        }
        log.info("Ürün {} için {} adet fiyat baremi yazıldı", p.getSlug(), tiers.size());
    }

    private ProductTypeResponse toResponse(ProductType p) {
        List<PriceRule> rules = priceRuleRepository
                .findByProductTypeIdOrderByMinQtyAsc(p.getId()).stream()
                .filter(r -> r.getOptionKey() == null)
                .toList();

        List<PriceTierDto> tierDtos = rules.stream()
                .map(r -> new PriceTierDto(
                        r.getMinQty(),
                        r.getMaxQty(),
                        r.getBasePrice() != null ? r.getBasePrice() : r.getUnitPrice()))
                .toList();

        BigDecimal basePrice = tierDtos.isEmpty() ? null : tierDtos.get(0).getPrice();

        return ProductTypeResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .slug(p.getSlug())
                .pricingModel(p.getPricingModel())
                .unit(p.getUnit())
                .hasFile(p.getHasFile())
                .minOrder(p.getMinOrder())
                .isActive(p.getIsActive())
                .description(p.getDescription())
                .imageUrl(p.getImageUrl())
                .basePrice(basePrice)
                .featured(p.getFeatured() != null && p.getFeatured())
                .badge(p.getBadge())
                .originalPrice(p.getOriginalPrice())
                .priceTiers(tierDtos)
                .build();
    }

    private static String asString(Object v) { return v == null ? null : v.toString(); }
    private static Integer asInt(Object v, Integer fallback) {
        if (v == null) return fallback;
        try { return Integer.parseInt(v.toString().trim()); }
        catch (Exception e) { return fallback; }
    }
    private static BigDecimal asBigDecimal(Object v) {
        if (v == null) return null;
        try { return new BigDecimal(v.toString().trim().replace(",", ".")); }
        catch (Exception e) { return null; }
    }
    private static Boolean asBoolean(Object v, Boolean fallback) {
        if (v == null) return fallback;
        String s = v.toString().trim().toLowerCase();
        return s.equals("true") || s.equals("1") || s.equals("on");
    }
}