package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.ReferenceRequest;
import com.ilbarslab.ardbackend.print.dto.response.ReferenceResponse;
import com.ilbarslab.ardbackend.print.entity.Reference;
import com.ilbarslab.ardbackend.print.repository.ReferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
        String logoUrl = resolveLogoUrl(logo, req.getLogoUrl(), null);

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
                .showText(req.getShowText() != null ? req.getShowText() : true)
                .displayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0)
                .build();

        return toResponse(referenceRepository.save(ref));
    }

    public ReferenceResponse update(UUID id, ReferenceRequest req, MultipartFile logo) throws IOException {
        Reference ref = referenceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Referans bulunamadı"));

        ref.setLogoUrl(resolveLogoUrl(logo, req.getLogoUrl(), ref.getLogoUrl()));

        ref.setName(req.getName());
        ref.setSector(req.getSector());
        ref.setCategory(req.getCategory());
        ref.setDescription(req.getDescription());
        if (req.getColor() != null)        ref.setColor(req.getColor());
        if (req.getAbbr() != null)         ref.setAbbr(req.getAbbr());
        if (req.getFeatured() != null)     ref.setFeatured(req.getFeatured());
        if (req.getActive() != null)       ref.setActive(req.getActive());
        if (req.getShowText() != null)     ref.setShowText(req.getShowText());
        if (req.getDisplayOrder() != null) ref.setDisplayOrder(req.getDisplayOrder());

        return toResponse(referenceRepository.save(ref));
    }

    public void delete(UUID id) {
        if (!referenceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Referans bulunamadı");
        }
        referenceRepository.deleteById(id);
    }

    public ReferenceResponse toggleActive(UUID id) {
        Reference ref = referenceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Referans bulunamadı"));
        ref.setActive(!ref.getActive());
        return toResponse(referenceRepository.save(ref));
    }

    /**
     * Logo URL'ini çöz:
     *  1) Dosya yüklendiyse R2'ye yükle, URL'ini dön
     *  2) Frontend logoUrl alanı doluysa onu kullan
     *  3) Aksi halde mevcut URL'i koru
     */
    private String resolveLogoUrl(MultipartFile logo, String reqUrl, String currentUrl) {
        if (logo != null && !logo.isEmpty()) {
            try {
                return storageService.uploadFile(logo, "references/logos");
            } catch (Exception e) {
                log.warn("Logo R2 upload hatası: {}", e.getMessage());
                // fail edersen url path'ine düş
            }
        }
        if (reqUrl != null && !reqUrl.isBlank()) {
            return reqUrl.trim();
        }
        return currentUrl;
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
                .showText(r.getShowText() != null ? r.getShowText() : true)
                .displayOrder(r.getDisplayOrder())
                .build();
    }
}