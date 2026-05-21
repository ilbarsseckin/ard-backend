package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.ReferenceRequest;
import com.ilbarslab.ardbackend.print.dto.response.ReferenceResponse;
import com.ilbarslab.ardbackend.print.entity.Reference;
import com.ilbarslab.ardbackend.print.repository.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReferenceService {

    private final ReferenceRepository referenceRepository;
    private final StorageService storageService;

    public List<ReferenceResponse> getAll() {
        return referenceRepository.findByActiveOrderByDisplayOrderAsc(true)
                .stream().map(this::toResponse).toList();
    }

    public List<ReferenceResponse> getByCategory(String category) {
        return referenceRepository.findByCategoryAndActiveOrderByDisplayOrderAsc(category, true)
                .stream().map(this::toResponse).toList();
    }

    public List<ReferenceResponse> getFeatured() {
        return referenceRepository.findByFeaturedAndActiveOrderByDisplayOrderAsc(true, true)
                .stream().map(this::toResponse).toList();
    }

    public ReferenceResponse create(ReferenceRequest req, MultipartFile logo) throws IOException {
        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            try {
                logoUrl = storageService.uploadFile(logo, "references/logos");
            } catch (Exception e) {
                // Logo upload başarısız olsa bile kaydet
                System.err.println("Logo upload hatası: " + e.getMessage());
            }
        }

        Reference ref = Reference.builder()
                .name(req.getName())
                .sector(req.getSector())
                .category(req.getCategory())
                .description(req.getDescription())
                .logoUrl(logoUrl)
                .color(req.getColor() != null ? req.getColor() : "#F4821F")
                .abbr(req.getAbbr())
                .featured(req.getFeatured() != null ? req.getFeatured() : false)
                .active(req.getActive() != null ? req.getActive() : true)
                .displayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0)
                .build();

        return toResponse(referenceRepository.save(ref));
    }

    public ReferenceResponse update(UUID id, ReferenceRequest req, MultipartFile logo) throws IOException {
        Reference ref = referenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Referans bulunamadı"));

        if (logo != null && !logo.isEmpty()) {
            try {
                ref.setLogoUrl(storageService.uploadFile(logo, "references/logos"));
            } catch (Exception e) {
                System.err.println("Logo upload hatası: " + e.getMessage());
            }
        }

        ref.setName(req.getName());
        ref.setSector(req.getSector());
        ref.setCategory(req.getCategory());
        ref.setDescription(req.getDescription());
        if (req.getColor() != null) ref.setColor(req.getColor());
        if (req.getAbbr() != null) ref.setAbbr(req.getAbbr());
        if (req.getFeatured() != null) ref.setFeatured(req.getFeatured());
        if (req.getActive() != null) ref.setActive(req.getActive());
        if (req.getDisplayOrder() != null) ref.setDisplayOrder(req.getDisplayOrder());

        return toResponse(referenceRepository.save(ref));
    }

    public void delete(UUID id) {
        referenceRepository.deleteById(id);
    }

    public ReferenceResponse toggleActive(UUID id) {
        Reference ref = referenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Referans bulunamadı"));
        ref.setActive(!ref.getActive());
        return toResponse(referenceRepository.save(ref));
    }

    private ReferenceResponse toResponse(Reference r) {
        return ReferenceResponse.builder()
                .id(r.getId())
                .name(r.getName())
                .sector(r.getSector())
                .category(r.getCategory())
                .description(r.getDescription())
                .logoUrl(r.getLogoUrl())
                .color(r.getColor())
                .abbr(r.getAbbr())
                .featured(r.getFeatured())
                .active(r.getActive())
                .displayOrder(r.getDisplayOrder())
                .build();
    }
}