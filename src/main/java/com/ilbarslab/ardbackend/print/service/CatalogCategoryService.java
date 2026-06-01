package com.ilbarslab.ardbackend.print.service;


import com.ilbarslab.ardbackend.print.dto.response.CatalogCategoryResponse;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogCategory;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogCategoryRepository;
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
public class CatalogCategoryService {

    private final CatalogCategoryRepository categoryRepo;
    private final CatalogProductRepository productRepo;

    // ─────────── READ ───────────

    @Transactional(readOnly = true)
    public List<CatalogCategoryResponse> getAll() {
        return categoryRepo.findAll().stream()
                .sorted(Comparator.comparing((CatalogCategory c) -> c.getSortOrder() == null ? 0 : c.getSortOrder())
                        .thenComparing(CatalogCategory::getName))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogCategoryResponse> getTree(boolean activeOnly) {
        List<CatalogCategory> all = activeOnly
                ? categoryRepo.findByActiveTrueOrderBySortOrderAsc()
                : categoryRepo.findAll();
        return buildTree(all, null);
    }

    @Transactional(readOnly = true)
    public CatalogCategoryResponse getById(UUID id) {
        return toResponse(categoryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı")));
    }

    @Transactional(readOnly = true)
    public CatalogCategoryResponse getBySlug(String slug) {
        return toResponse(categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı: " + slug)));
    }

    // ─────────── CREATE ───────────

    @Transactional
    public CatalogCategoryResponse create(Map<String, Object> body) {
        String slug = asString(body.get("slug"));
        String name = asString(body.get("name"));

        if (slug == null || slug.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug zorunlu");
        }
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "İsim zorunlu");
        }
        validateSlugFormat(slug);

        if (categoryRepo.findBySlug(slug).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu slug zaten kullanılıyor: " + slug);
        }

        CatalogCategory parent = null;
        if (body.get("parentId") != null) {
            UUID pid = parseUuid(body.get("parentId"));
            parent = categoryRepo.findById(pid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Üst kategori bulunamadı"));
        }

        CatalogCategory cat = CatalogCategory.builder()
                .slug(slug)
                .name(name)
                .icon(asString(body.get("icon")))
                .tagline(asString(body.get("tagline")))
                .parent(parent)
                .sortOrder(asInt(body.get("sortOrder"), 0))
                .active(asBoolean(body.get("active"), true))
                .build();

        cat = categoryRepo.save(cat);
        log.info("Kategori oluşturuldu: {} ({})", cat.getName(), cat.getSlug());
        return toResponse(cat);
    }

    // ─────────── UPDATE ───────────

    @Transactional
    public CatalogCategoryResponse update(UUID id, Map<String, Object> body) {
        CatalogCategory cat = categoryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı"));

        if (body.containsKey("slug")) {
            String newSlug = asString(body.get("slug"));
            if (newSlug != null && !newSlug.equals(cat.getSlug())) {
                validateSlugFormat(newSlug);
                if (categoryRepo.findBySlug(newSlug).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu slug zaten kullanılıyor: " + newSlug);
                }
                cat.setSlug(newSlug);
            }
        }

        if (body.containsKey("name"))      cat.setName(asString(body.get("name")));
        if (body.containsKey("icon"))      cat.setIcon(asString(body.get("icon")));
        if (body.containsKey("tagline"))   cat.setTagline(asString(body.get("tagline")));
        if (body.containsKey("sortOrder")) cat.setSortOrder(asInt(body.get("sortOrder"), cat.getSortOrder()));
        if (body.containsKey("active"))    cat.setActive(asBoolean(body.get("active"), cat.getActive()));

        // Parent değiştirme (null gönderilirse root'a taşır)
        if (body.containsKey("parentId")) {
            Object pIdObj = body.get("parentId");
            if (pIdObj == null) {
                cat.setParent(null);
            } else {
                UUID parentId = parseUuid(pIdObj);
                if (parentId.equals(id)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kategori kendi parent'ı olamaz");
                }
                if (isDescendant(id, parentId)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Döngüsel parent ataması yapılamaz");
                }
                CatalogCategory parent = categoryRepo.findById(parentId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Üst kategori bulunamadı"));
                cat.setParent(parent);
            }
        }

        cat = categoryRepo.save(cat);
        return toResponse(cat);
    }

    // ─────────── DELETE ───────────

    @Transactional
    public void delete(UUID id) {
        CatalogCategory cat = categoryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı"));

        long childCount = categoryRepo.findByParentIdOrderBySortOrderAsc(id).size();
        if (childCount > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bu kategorinin " + childCount + " alt kategorisi var, önce onları silin veya taşıyın");
        }

        long productCount = productRepo.findByCategoryIdOrderBySortOrderAsc(id).size();
        if (productCount > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bu kategoride " + productCount + " ürün var, önce onları başka kategoriye taşıyın");
        }

        categoryRepo.delete(cat);
        log.info("Kategori silindi: {}", cat.getName());
    }

    // ─────────── TOGGLE ───────────

    @Transactional
    public CatalogCategoryResponse toggleActive(UUID id) {
        CatalogCategory cat = categoryRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kategori bulunamadı"));
        cat.setActive(!Boolean.TRUE.equals(cat.getActive()));
        cat = categoryRepo.save(cat);
        return toResponse(cat);
    }

    // ─────────── REORDER ───────────

    @Transactional
    public int reorder(List<Map<String, Object>> orderList) {
        int updated = 0;
        for (Map<String, Object> item : orderList) {
            try {
                UUID id = parseUuid(item.get("id"));
                Integer order = asInt(item.get("sortOrder"), null);
                if (order == null) continue;
                Optional<CatalogCategory> opt = categoryRepo.findById(id);
                if (opt.isPresent()) {
                    CatalogCategory c = opt.get();
                    c.setSortOrder(order);
                    categoryRepo.save(c);
                    updated++;
                }
            } catch (Exception e) {
                log.warn("Reorder item atlandı: {}", item, e);
            }
        }
        return updated;
    }

    // ─────────── private helpers ───────────

    private List<CatalogCategoryResponse> buildTree(List<CatalogCategory> all, UUID parentId) {
        return all.stream()
                .filter(c -> {
                    UUID pId = c.getParent() != null ? c.getParent().getId() : null;
                    return Objects.equals(pId, parentId);
                })
                .sorted(Comparator.comparing((CatalogCategory c) -> c.getSortOrder() == null ? 0 : c.getSortOrder())
                        .thenComparing(CatalogCategory::getName))
                .map(c -> {
                    CatalogCategoryResponse resp = toResponse(c);
                    resp.setChildren(buildTree(all, c.getId()));
                    return resp;
                })
                .toList();
    }

    private boolean isDescendant(UUID rootId, UUID checkId) {
        List<CatalogCategory> children = categoryRepo.findByParentIdOrderBySortOrderAsc(rootId);
        for (CatalogCategory child : children) {
            if (child.getId().equals(checkId)) return true;
            if (isDescendant(child.getId(), checkId)) return true;
        }
        return false;
    }

    private void validateSlugFormat(String slug) {
        if (!slug.matches("[a-z0-9-]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Slug sadece küçük harf, rakam ve tire içerebilir");
        }
    }

    private CatalogCategoryResponse toResponse(CatalogCategory c) {
        long childCount = categoryRepo.findByParentIdOrderBySortOrderAsc(c.getId()).size();
        long productCount = productRepo.findByCategoryIdOrderBySortOrderAsc(c.getId()).size();

        return CatalogCategoryResponse.builder()
                .id(c.getId())
                .slug(c.getSlug())
                .name(c.getName())
                .icon(c.getIcon())
                .tagline(c.getTagline())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .parentName(c.getParent() != null ? c.getParent().getName() : null)
                .sortOrder(c.getSortOrder())
                .active(c.getActive())
                .childCount(childCount)
                .productCount(productCount)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    // Type conversion helpers (ProductService ile aynı patern)
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

    private static UUID parseUuid(Object v) {
        if (v == null) return null;
        try { return UUID.fromString(v.toString().trim()); }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz UUID: " + v);
        }
    }
}