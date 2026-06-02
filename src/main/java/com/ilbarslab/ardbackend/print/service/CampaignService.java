package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.CampaignRequest;
import com.ilbarslab.ardbackend.print.dto.response.CampaignResponse;
import com.ilbarslab.ardbackend.print.entity.Campaign;
import com.ilbarslab.ardbackend.print.repository.CampaignRepository;
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
public class CampaignService {

    private final CampaignRepository repo;

    // ─────────── PUBLIC ───────────

    /** Vitrin için — sadece aktif ve tarih aralığındaki kampanyalar */
    @Transactional(readOnly = true)
    public List<CampaignResponse> listActive() {
        Instant now = Instant.now();
        return repo.findByActiveTrueOrderBySortOrderAscCreatedAtAsc().stream()
            .filter(c -> c.getStartsAt() == null || !now.isBefore(c.getStartsAt()))
            .filter(c -> c.getEndsAt() == null   || !now.isAfter(c.getEndsAt()))
            .map(this::toResponse)
            .toList();
    }

    // ─────────── ADMIN ───────────

    @Transactional(readOnly = true)
    public List<CampaignResponse> listAll() {
        return repo.findAllByOrderBySortOrderAscCreatedAtAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public CampaignResponse getById(UUID id) {
        return toResponse(find(id));
    }

    @Transactional
    public CampaignResponse create(CampaignRequest req) {
        validate(req);
        Campaign c = Campaign.builder()
            .label(req.getLabel())
            .title(req.getTitle())
            .description(req.getDescription())
            .badgeText(req.getBadgeText())
            .badgeColor(req.getBadgeColor())
            .imageUrl(req.getImageUrl())
            .mobileImageUrl(req.getMobileImageUrl())
            .backgroundColor(req.getBackgroundColor())
            .ctaText(req.getCtaText())
            .ctaLink(req.getCtaLink())
            .sortOrder(req.getSortOrder() == null ? 0 : req.getSortOrder())
            .active(req.getActive() == null ? Boolean.TRUE : req.getActive())
            .startsAt(req.getStartsAt())
            .endsAt(req.getEndsAt())
            .build();
        return toResponse(repo.save(c));
    }

    @Transactional
    public CampaignResponse update(UUID id, CampaignRequest req) {
        validate(req);
        Campaign c = find(id);
        c.setLabel(req.getLabel());
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setBadgeText(req.getBadgeText());
        c.setBadgeColor(req.getBadgeColor());
        c.setImageUrl(req.getImageUrl());
        c.setMobileImageUrl(req.getMobileImageUrl());
        c.setBackgroundColor(req.getBackgroundColor());
        c.setCtaText(req.getCtaText());
        c.setCtaLink(req.getCtaLink());
        if (req.getSortOrder() != null) c.setSortOrder(req.getSortOrder());
        if (req.getActive() != null) c.setActive(req.getActive());
        c.setStartsAt(req.getStartsAt());
        c.setEndsAt(req.getEndsAt());
        return toResponse(repo.save(c));
    }

    @Transactional
    public CampaignResponse toggleActive(UUID id) {
        Campaign c = find(id);
        c.setActive(!Boolean.TRUE.equals(c.getActive()));
        return toResponse(repo.save(c));
    }

    @Transactional
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    @Transactional
    public void reorder(List<UUID> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            UUID id = orderedIds.get(i);
            Campaign c = repo.findById(id).orElse(null);
            if (c != null) {
                c.setSortOrder(i);
                repo.save(c);
            }
        }
    }

    // ─────────── private ───────────

    private Campaign find(UUID id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kampanya bulunamadı"));
    }

    private void validate(CampaignRequest req) {
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Başlık zorunlu");
        }
        if (req.getImageUrl() == null || req.getImageUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Görsel zorunlu");
        }
    }

    private CampaignResponse toResponse(Campaign c) {
        return CampaignResponse.builder()
            .id(c.getId())
            .label(c.getLabel())
            .title(c.getTitle())
            .description(c.getDescription())
            .badgeText(c.getBadgeText())
            .badgeColor(c.getBadgeColor())
            .imageUrl(c.getImageUrl())
            .mobileImageUrl(c.getMobileImageUrl())
            .backgroundColor(c.getBackgroundColor())
            .ctaText(c.getCtaText())
            .ctaLink(c.getCtaLink())
            .sortOrder(c.getSortOrder())
            .active(c.getActive())
            .startsAt(c.getStartsAt())
            .endsAt(c.getEndsAt())
            .createdAt(c.getCreatedAt())
            .updatedAt(c.getUpdatedAt())
            .build();
    }
}
