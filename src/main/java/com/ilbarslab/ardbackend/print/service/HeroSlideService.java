package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.HeroSlideRequest;
import com.ilbarslab.ardbackend.print.dto.response.HeroSlideResponse;
import com.ilbarslab.ardbackend.print.entity.HeroLayout;
import com.ilbarslab.ardbackend.print.entity.HeroSlide;
import com.ilbarslab.ardbackend.print.repository.HeroSlideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HeroSlideService {

    private final HeroSlideRepository repo;

    // ─────────── PUBLIC ───────────

    /** Public ana sayfa için — sadece aktif ve tarih aralığındaki slide'lar */
    @Transactional(readOnly = true)
    public List<HeroSlideResponse> listActive() {
        Instant now = Instant.now();
        return repo.findByActiveTrueOrderBySortOrderAscCreatedAtAsc().stream()
            .filter(s -> s.getStartsAt() == null || !now.isBefore(s.getStartsAt()))
            .filter(s -> s.getEndsAt() == null   || !now.isAfter(s.getEndsAt()))
            .map(this::toResponse)
            .toList();
    }

    // ─────────── ADMIN ───────────

    @Transactional(readOnly = true)
    public List<HeroSlideResponse> listAll() {
        return repo.findAllByOrderBySortOrderAscCreatedAtAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public HeroSlideResponse getById(UUID id) {
        return toResponse(find(id));
    }

    @Transactional
    public HeroSlideResponse create(HeroSlideRequest req) {
        validate(req);
        HeroSlide s = HeroSlide.builder()
            .label(req.getLabel())
            .title(req.getTitle())
            .description(req.getDescription())
            .ctaText(req.getCtaText())
            .ctaLink(req.getCtaLink())
            .imageUrl(req.getImageUrl())
            .mobileImageUrl(req.getMobileImageUrl())
            .backgroundColor(req.getBackgroundColor())
            .layout(parseLayout(req.getLayout()))
            .sortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder())
            .active(req.getActive() == null ? Boolean.TRUE : req.getActive())
            .startsAt(req.getStartsAt())
            .endsAt(req.getEndsAt())
            .build();
        return toResponse(repo.save(s));
    }

    @Transactional
    public HeroSlideResponse update(UUID id, HeroSlideRequest req) {
        validate(req);
        HeroSlide s = find(id);
        s.setLabel(req.getLabel());
        s.setTitle(req.getTitle());
        s.setDescription(req.getDescription());
        s.setCtaText(req.getCtaText());
        s.setCtaLink(req.getCtaLink());
        s.setImageUrl(req.getImageUrl());
        s.setMobileImageUrl(req.getMobileImageUrl());
        s.setBackgroundColor(req.getBackgroundColor());
        s.setLayout(parseLayout(req.getLayout()));
        if (req.getSortOrder() != null) s.setSortOrder(req.getSortOrder());
        if (req.getActive() != null) s.setActive(req.getActive());
        s.setStartsAt(req.getStartsAt());
        s.setEndsAt(req.getEndsAt());
        return toResponse(repo.save(s));
    }

    @Transactional
    public HeroSlideResponse toggleActive(UUID id) {
        HeroSlide s = find(id);
        s.setActive(!Boolean.TRUE.equals(s.getActive()));
        return toResponse(repo.save(s));
    }

    @Transactional
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    @Transactional
    public void reorder(List<UUID> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            UUID id = orderedIds.get(i);
            HeroSlide s = repo.findById(id).orElse(null);
            if (s != null) {
                s.setSortOrder(i);
                repo.save(s);
            }
        }
    }

    // ─────────── private ───────────

    private HeroSlide find(UUID id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slide bulunamadı"));
    }

    private void validate(HeroSlideRequest req) {
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Başlık zorunlu");
        }
        if (req.getImageUrl() == null || req.getImageUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resim zorunlu");
        }
    }

    private HeroLayout parseLayout(String s) {
        if (s == null || s.isBlank()) return HeroLayout.SPLIT_LEFT;
        try { return HeroLayout.valueOf(s.toUpperCase()); }
        catch (Exception e) { return HeroLayout.SPLIT_LEFT; }
    }

    private HeroSlideResponse toResponse(HeroSlide s) {
        return HeroSlideResponse.builder()
            .id(s.getId())
            .label(s.getLabel())
            .title(s.getTitle())
            .description(s.getDescription())
            .ctaText(s.getCtaText())
            .ctaLink(s.getCtaLink())
            .imageUrl(s.getImageUrl())
            .mobileImageUrl(s.getMobileImageUrl())
            .backgroundColor(s.getBackgroundColor())
            .layout(s.getLayout() == null ? "SPLIT_LEFT" : s.getLayout().name())
            .sortOrder(s.getSortOrder())
            .active(s.getActive())
            .startsAt(s.getStartsAt())
            .endsAt(s.getEndsAt())
            .createdAt(s.getCreatedAt())
            .updatedAt(s.getUpdatedAt())
            .build();
    }
}
