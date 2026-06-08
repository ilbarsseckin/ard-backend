package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.AnnouncementBarRequest;
import com.ilbarslab.ardbackend.print.dto.response.AnnouncementBarResponse;
import com.ilbarslab.ardbackend.print.entity.AnnouncementBar;
import com.ilbarslab.ardbackend.print.repository.AnnouncementBarRepository;
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
public class AnnouncementBarService {

    private final AnnouncementBarRepository repo;

    public List<AnnouncementBarResponse> listActive() {
        Instant now = Instant.now();
        return repo.findByActiveTrueOrderBySortOrderAsc().stream()
            .filter(b -> b.getEndsAt() == null || now.isBefore(b.getEndsAt()))
            .map(this::toResponse)
            .toList();
    }

    public List<AnnouncementBarResponse> listAll() {
        return repo.findAllByOrderBySortOrderAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public AnnouncementBarResponse create(AnnouncementBarRequest req) {
        if (req.getMessage() == null || req.getMessage().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mesaj zorunlu");
        AnnouncementBar bar = AnnouncementBar.builder()
            .message(req.getMessage())
            .subMessage(req.getSubMessage())
            .couponCode(req.getCouponCode())
            .bgColor(req.getBgColor() != null ? req.getBgColor() : "#F4821F")
            .textColor(req.getTextColor() != null ? req.getTextColor() : "#FFFFFF")
            .endsAt(req.getEndsAt())
            .active(req.getActive() != null ? req.getActive() : Boolean.TRUE)
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        return toResponse(repo.save(bar));
    }

    @Transactional
    public AnnouncementBarResponse update(UUID id, AnnouncementBarRequest req) {
        AnnouncementBar bar = find(id);
        if (req.getMessage() != null) bar.setMessage(req.getMessage());
        if (req.getSubMessage() != null) bar.setSubMessage(req.getSubMessage());
        if (req.getCouponCode() != null) bar.setCouponCode(req.getCouponCode());
        if (req.getBgColor() != null) bar.setBgColor(req.getBgColor());
        if (req.getTextColor() != null) bar.setTextColor(req.getTextColor());
        if (req.getEndsAt() != null) bar.setEndsAt(req.getEndsAt());
        if (req.getActive() != null) bar.setActive(req.getActive());
        if (req.getSortOrder() != null) bar.setSortOrder(req.getSortOrder());
        return toResponse(repo.save(bar));
    }

    @Transactional
    public void delete(UUID id) {
        repo.deleteById(id);
    }

    @Transactional
    public AnnouncementBarResponse toggle(UUID id) {
        AnnouncementBar bar = find(id);
        bar.setActive(!Boolean.TRUE.equals(bar.getActive()));
        return toResponse(repo.save(bar));
    }

    private AnnouncementBar find(UUID id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Banner bulunamadı"));
    }

    private AnnouncementBarResponse toResponse(AnnouncementBar b) {
        return AnnouncementBarResponse.builder()
            .id(b.getId())
            .message(b.getMessage())
            .subMessage(b.getSubMessage())
            .couponCode(b.getCouponCode())
            .bgColor(b.getBgColor())
            .textColor(b.getTextColor())
            .endsAt(b.getEndsAt())
            .active(b.getActive())
            .sortOrder(b.getSortOrder())
            .createdAt(b.getCreatedAt())
            .updatedAt(b.getUpdatedAt())
            .build();
    }
}
