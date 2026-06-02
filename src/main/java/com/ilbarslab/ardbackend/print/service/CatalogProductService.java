package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.*;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.*;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogProductService {

    private final CatalogProductRepository productRepo;
    private final CatalogCategoryRepository categoryRepo;
    private final CatalogBrandRepository brandRepo;
    private final CatalogAttributeRepository attributeRepo;
    private final CatalogAttributeOptionRepository optionRepo;
    private final CatalogProductAttributeValueRepository pavRepo;
    private final CatalogProductTierRepository tierRepo;
    private final CatalogProductImageRepository imageRepo;

    // ─────────── READ ───────────

    @Transactional(readOnly = true)
    public List<CatalogProductSummaryResponse> list(UUID categoryId, UUID brandId, Boolean activeOnly) {
        List<CatalogProduct> products;
        if (categoryId != null) {
            products = Boolean.TRUE.equals(activeOnly)
                    ? productRepo.findByCategoryIdAndActiveTrueOrderBySortOrderAsc(categoryId)
                    : productRepo.findByCategoryIdOrderBySortOrderAsc(categoryId);
        } else if (brandId != null) {
            products = productRepo.findByBrandIdOrderBySortOrderAsc(brandId);
            if (Boolean.TRUE.equals(activeOnly)) {
                products = products.stream().filter(p -> Boolean.TRUE.equals(p.getActive())).toList();
            }
        } else {
            products = productRepo.findAll();
            if (Boolean.TRUE.equals(activeOnly)) {
                products = products.stream().filter(p -> Boolean.TRUE.equals(p.getActive())).toList();
            }
        }
        return products.stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogProductSummaryResponse> getFeatured() {
        return productRepo.findByFeaturedTrueAndActiveTrueOrderBySortOrderAsc().stream()
                .map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public CatalogProductResponse getById(UUID id) {
        CatalogProduct p = productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        return toDetail(p);
    }

    @Transactional(readOnly = true)
    public CatalogProductResponse getBySlug(String slug) {
        CatalogProduct p = productRepo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + slug));
        return toDetail(p);
    }

    // ─────────── BEST SELLERS ───────────

    @Transactional(readOnly = true)
    public List<CatalogProductSummaryResponse> listBestSellers(int limit) {
        if (limit <= 0) limit = 8;
        if (limit > 50) limit = 50;

        List<UUID> ids = productRepo.findBestSellerProductIds(limit);
        if (ids.isEmpty()) return List.of();

        Map<UUID, Long> orderCounts = new HashMap<>();
        for (Object[] row : productRepo.countOrdersByProduct()) {
            Object idObj = row[0];
            UUID pid = idObj instanceof UUID u ? u : UUID.fromString(idObj.toString());
            Long cnt = row[1] == null ? 0L : ((Number) row[1]).longValue();
            orderCounts.put(pid, cnt);
        }

        return ids.stream()
                .map(id -> productRepo.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(p -> {
                    CatalogProductSummaryResponse r = toSummary(p);
                    r.setOrderCount(orderCounts.getOrDefault(p.getId(), 0L));
                    return r;
                })
                .toList();
    }

    // ─────────── CREATE ───────────

    @Transactional
    public CatalogProductResponse create(Map<String, Object> body) {
        String slug = asString(body.get("slug"));
        String name = asString(body.get("name"));
        Object catIdObj = body.get("categoryId");

        if (slug == null || slug.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug zorunlu");
        if (name == null || name.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "İsim zorunlu");
        if (catIdObj == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori zorunlu");

        validateSlugFormat(slug);
        if (productRepo.findBySlug(slug).isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu slug zaten kullanılıyor: " + slug);

        CatalogCategory category = categoryRepo.findById(parseUuid(catIdObj))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori bulunamadı"));

        CatalogBrand brand = null;
        if (body.get("brandId") != null) {
            brand = brandRepo.findById(parseUuid(body.get("brandId")))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marka bulunamadı"));
        }

        CatalogProduct product = CatalogProduct.builder()
                .category(category)
                .brand(brand)
                .slug(slug)
                .name(name)
                .shortDesc(asString(body.get("shortDesc")))
                .longDesc(asString(body.get("longDesc")))
                .featured(asBoolean(body.get("featured"), false))
                .badge(asString(body.get("badge")))
                .originalPrice(asBigDecimal(body.get("originalPrice")))
                .active(asBoolean(body.get("active"), true))
                .sortOrder(asInt(body.get("sortOrder"), 0))
                .build();

        product = productRepo.save(product);

        if (body.containsKey("attributeValues")) saveAttributeValues(product, body.get("attributeValues"));
        if (body.containsKey("tiers"))           saveTiers(product, body.get("tiers"));
        if (body.containsKey("images"))          saveImages(product, body.get("images"));

        log.info("Ürün oluşturuldu: {} ({})", product.getName(), product.getSlug());
        return toDetail(product);
    }

    // ─────────── UPDATE ───────────

    @Transactional
    public CatalogProductResponse update(UUID id, Map<String, Object> body) {
        CatalogProduct product = productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));

        if (body.containsKey("slug")) {
            String newSlug = asString(body.get("slug"));
            if (newSlug != null && !newSlug.equals(product.getSlug())) {
                validateSlugFormat(newSlug);
                if (productRepo.findBySlug(newSlug).isPresent())
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu slug zaten kullanılıyor: " + newSlug);
                product.setSlug(newSlug);
            }
        }
        if (body.containsKey("name"))          product.setName(asString(body.get("name")));
        if (body.containsKey("shortDesc"))     product.setShortDesc(asString(body.get("shortDesc")));
        if (body.containsKey("longDesc"))      product.setLongDesc(asString(body.get("longDesc")));
        if (body.containsKey("featured"))      product.setFeatured(asBoolean(body.get("featured"), product.getFeatured()));
        if (body.containsKey("badge"))         product.setBadge(asString(body.get("badge")));
        if (body.containsKey("originalPrice")) product.setOriginalPrice(asBigDecimal(body.get("originalPrice")));
        if (body.containsKey("active"))        product.setActive(asBoolean(body.get("active"), product.getActive()));
        if (body.containsKey("sortOrder"))     product.setSortOrder(asInt(body.get("sortOrder"), product.getSortOrder()));

        if (body.containsKey("categoryId")) {
            Object catIdObj = body.get("categoryId");
            if (catIdObj != null) {
                CatalogCategory newCat = categoryRepo.findById(parseUuid(catIdObj))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori bulunamadı"));
                product.setCategory(newCat);
            }
        }

        if (body.containsKey("brandId")) {
            Object brandIdObj = body.get("brandId");
            if (brandIdObj == null) {
                product.setBrand(null);
            } else {
                CatalogBrand newBrand = brandRepo.findById(parseUuid(brandIdObj))
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marka bulunamadı"));
                product.setBrand(newBrand);
            }
        }

        product = productRepo.save(product);

        if (body.containsKey("attributeValues")) saveAttributeValues(product, body.get("attributeValues"));
        if (body.containsKey("tiers"))           saveTiers(product, body.get("tiers"));
        if (body.containsKey("images"))          saveImages(product, body.get("images"));

        return toDetail(product);
    }

    // ─────────── DELETE ───────────

    @Transactional
    public void delete(UUID id) {
        CatalogProduct product = productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));

        imageRepo.deleteByProductId(id);
        tierRepo.deleteByProductId(id);
        pavRepo.deleteByProductId(id);
        productRepo.delete(product);

        log.info("Ürün silindi: {}", product.getName());
    }

    @Transactional
    public CatalogProductResponse toggleActive(UUID id) {
        CatalogProduct product = productRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        product.setActive(!Boolean.TRUE.equals(product.getActive()));
        product = productRepo.save(product);
        return toDetail(product);
    }

    // ─────────── IMAGE ENDPOINTS ───────────

    @Transactional
    public CatalogProductImageResponse addImage(UUID productId, Map<String, Object> body) {
        CatalogProduct product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        String url = asString(body.get("url"));
        if (url == null || url.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resim URL'i zorunlu");

        int nextOrder = imageRepo.findByProductIdOrderBySortOrderAsc(productId).size();

        CatalogProductImage img = CatalogProductImage.builder()
                .product(product)
                .url(url)
                .altText(asString(body.get("altText")))
                .sortOrder(asInt(body.get("sortOrder"), nextOrder))
                .build();
        img = imageRepo.save(img);
        return toImageResponse(img);
    }

    @Transactional
    public void deleteImage(UUID imageId) {
        CatalogProductImage img = imageRepo.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resim bulunamadı"));
        imageRepo.delete(img);
    }

    // ─────────── private: kayıt yöneticileri ───────────

    @SuppressWarnings("unchecked")
    private void saveAttributeValues(CatalogProduct product, Object rawData) {
        pavRepo.deleteByProductId(product.getId());
        pavRepo.flush();

        if (!(rawData instanceof List<?> list)) return;

        for (Object item : list) {
            if (!(item instanceof Map<?,?> entry)) continue;

            Object attrIdObj = entry.get("attributeId");
            Object optIdsObj = entry.get("optionIds");
            if (attrIdObj == null || !(optIdsObj instanceof List<?> optIds)) continue;

            UUID attrId = parseUuid(attrIdObj);
            CatalogAttribute attr = attributeRepo.findById(attrId).orElse(null);
            if (attr == null) continue;

            for (Object optIdObj : optIds) {
                UUID optId = parseUuid(optIdObj);
                CatalogAttributeOption opt = optionRepo.findById(optId).orElse(null);
                if (opt == null) continue;
                CatalogProductAttributeValue pav = CatalogProductAttributeValue.builder()
                        .product(product)
                        .attribute(attr)
                        .option(opt)
                        .build();
                pavRepo.save(pav);
            }
        }
    }

    private void saveTiers(CatalogProduct product, Object rawData) {
        tierRepo.deleteByProductId(product.getId());
        tierRepo.flush();

        if (!(rawData instanceof List<?> list)) return;

        int idx = 0;
        for (Object item : list) {
            if (!(item instanceof Map<?,?> entry)) continue;
            Integer qty = asInt(entry.get("qty"), null);
            BigDecimal price = asBigDecimal(entry.get("priceUsd"));
            if (qty == null || qty < 1 || price == null || price.compareTo(BigDecimal.ZERO) <= 0) continue;
            CatalogProductTier tier = CatalogProductTier.builder()
                    .product(product)
                    .qty(qty)
                    .priceUsd(price)
                    .sortOrder(asInt(entry.get("sortOrder"), idx++))
                    .build();
            tierRepo.save(tier);
        }
    }

    private void saveImages(CatalogProduct product, Object rawData) {
        imageRepo.deleteByProductId(product.getId());
        imageRepo.flush();

        if (!(rawData instanceof List<?> list)) return;

        int idx = 0;
        for (Object item : list) {
            if (!(item instanceof Map<?,?> entry)) continue;
            String url = asString(entry.get("url"));
            if (url == null || url.isBlank()) continue;
            CatalogProductImage img = CatalogProductImage.builder()
                    .product(product)
                    .url(url)
                    .altText(asString(entry.get("altText")))
                    .sortOrder(asInt(entry.get("sortOrder"), idx++))
                    .build();
            imageRepo.save(img);
        }
    }

    // ─────────── private: response builders ───────────

    private CatalogProductSummaryResponse toSummary(CatalogProduct p) {
        List<CatalogProductImage> images = imageRepo.findByProductIdOrderBySortOrderAsc(p.getId());
        List<CatalogProductTier> tiers = tierRepo.findByProductIdOrderByQtyAsc(p.getId());

        return CatalogProductSummaryResponse.builder()
                .id(p.getId())
                .slug(p.getSlug())
                .name(p.getName())
                .shortDesc(p.getShortDesc())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .brandId(p.getBrand() != null ? p.getBrand().getId() : null)
                .brandName(p.getBrand() != null ? p.getBrand().getName() : null)
                .mainImageUrl(images.size() > 0 ? images.get(0).getUrl() : null)
                .hoverImageUrl(images.size() > 1 ? images.get(1).getUrl() : null)
                .minPriceUsd(tiers.isEmpty() ? null : tiers.get(0).getPriceUsd())
                .minPriceQty(tiers.isEmpty() ? null : tiers.get(0).getQty())
                .featured(p.getFeatured())
                .badge(p.getBadge())
                .originalPrice(p.getOriginalPrice())
                .active(p.getActive())
                .sortOrder(p.getSortOrder())
                .build();
    }

    private CatalogProductResponse toDetail(CatalogProduct p) {
        List<CatalogProductTierResponse> tiers = tierRepo
                .findByProductIdOrderByQtyAsc(p.getId()).stream()
                .map(t -> CatalogProductTierResponse.builder()
                        .id(t.getId())
                        .qty(t.getQty())
                        .priceUsd(t.getPriceUsd())
                        .sortOrder(t.getSortOrder())
                        .build())
                .toList();

        List<CatalogProductImageResponse> images = imageRepo
                .findByProductIdOrderBySortOrderAsc(p.getId()).stream()
                .map(this::toImageResponse)
                .toList();

        List<CatalogProductAttributeBlock> attrBlocks = buildAttributeBlocks(p);

        return CatalogProductResponse.builder()
                .id(p.getId())
                .slug(p.getSlug())
                .name(p.getName())
                .shortDesc(p.getShortDesc())
                .longDesc(p.getLongDesc())
                .categoryId(p.getCategory().getId())
                .categoryName(p.getCategory().getName())
                .categorySlug(p.getCategory().getSlug())
                .categoryIcon(p.getCategory().getIcon())
                .brandId(p.getBrand() != null ? p.getBrand().getId() : null)
                .brandSlug(p.getBrand() != null ? p.getBrand().getSlug() : null)
                .brandName(p.getBrand() != null ? p.getBrand().getName() : null)
                .brandLogoUrl(p.getBrand() != null ? p.getBrand().getLogoUrl() : null)
                .attributes(attrBlocks)
                .tiers(tiers)
                .images(images)
                .featured(p.getFeatured())
                .badge(p.getBadge())
                .originalPrice(p.getOriginalPrice())
                .active(p.getActive())
                .sortOrder(p.getSortOrder())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private List<CatalogProductAttributeBlock> buildAttributeBlocks(CatalogProduct p) {
        UUID catId = p.getCategory().getId();
        List<CatalogAttribute> attrs = attributeRepo.findByCategoryIdOrderBySortOrderAsc(catId);

        // Üst kategori fallback: alt kategoride öznitelik yoksa üst kategoriye bak
        if (attrs.isEmpty() && p.getCategory().getParent() != null) {
            attrs = attributeRepo.findByCategoryIdOrderBySortOrderAsc(
                    p.getCategory().getParent().getId());
        }

        List<CatalogProductAttributeValue> pavs = pavRepo.findByProductId(p.getId());

        Map<UUID, List<CatalogAttributeOption>> optionsByAttr = new HashMap<>();
        for (CatalogProductAttributeValue pav : pavs) {
            optionsByAttr
                    .computeIfAbsent(pav.getAttribute().getId(), k -> new ArrayList<>())
                    .add(pav.getOption());
        }

        return attrs.stream().map(attr -> {
            List<CatalogAttributeOption> selected = optionsByAttr.getOrDefault(attr.getId(), List.of());
            List<CatalogAttributeOptionResponse> selectedDtos = selected.stream()
                    .sorted(Comparator.comparing(o -> o.getSortOrder() == null ? 0 : o.getSortOrder()))
                    .map(o -> CatalogAttributeOptionResponse.builder()
                            .id(o.getId())
                            .attributeId(o.getAttribute().getId())
                            .value(o.getValue())
                            .colorHex(o.getColorHex())
                            .sortOrder(o.getSortOrder())
                            .priceModifier(o.getPriceModifier())
                            .build())
                    .toList();
            return CatalogProductAttributeBlock.builder()
                    .attributeId(attr.getId())
                    .attrKey(attr.getAttrKey())
                    .label(attr.getLabel())
                    .inputType(attr.getInputType())
                    .required(attr.getRequired())
                    .sortOrder(attr.getSortOrder())
                    .selectedOptions(selectedDtos)
                    .build();
        }).toList();
    }

    private CatalogProductImageResponse toImageResponse(CatalogProductImage img) {
        return CatalogProductImageResponse.builder()
                .id(img.getId())
                .url(img.getUrl())
                .altText(img.getAltText())
                .sortOrder(img.getSortOrder())
                .build();
    }

    // ─────────── private: helpers ───────────

    private void validateSlugFormat(String slug) {
        if (!slug.matches("[a-z0-9-]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Slug sadece küçük harf, rakam ve tire içerebilir");
        }
    }

    private static String asString(Object v) {
        return v == null ? null : v.toString();
    }

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

    private static UUID parseUuid(Object v) {
        if (v == null) return null;
        try { return UUID.fromString(v.toString().trim()); }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz UUID: " + v);
        }
    }
}