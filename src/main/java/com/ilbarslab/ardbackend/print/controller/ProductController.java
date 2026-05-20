package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.PriceCalculateRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.PriceCalculateResponse;
import com.ilbarslab.ardbackend.print.dto.response.ProductTypeResponse;
import com.ilbarslab.ardbackend.print.service.PricingService;
import com.ilbarslab.ardbackend.print.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final PricingService pricingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductTypeResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(productService.getAllActive()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductTypeResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(productService.getBySlug(slug)));
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<ApiResponse<PriceCalculateResponse>> calculatePrice(
            @Valid @RequestBody PriceCalculateRequest request) {
        PriceCalculateResponse response = pricingService.calculate(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
