package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.ProductTypeResponse;
import com.ilbarslab.ardbackend.print.entity.ProductType;
import com.ilbarslab.ardbackend.print.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductTypeRepository productTypeRepository;

    public List<ProductTypeResponse> getAllActive() {
        return productTypeRepository.findByIsActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ProductTypeResponse> getAll() {
        return productTypeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductTypeResponse getBySlug(String slug) {
        ProductType product = productTypeRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + slug));
        return toResponse(product);
    }

    public void toggleActive(UUID id) {
        ProductType product = productTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı"));
        product.setIsActive(!product.getIsActive());
        productTypeRepository.save(product);
    }

    private ProductTypeResponse toResponse(ProductType p) {
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
                .build();
    }
}
