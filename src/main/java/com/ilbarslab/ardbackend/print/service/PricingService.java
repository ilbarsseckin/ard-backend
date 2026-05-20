package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.PriceCalculateRequest;
import com.ilbarslab.ardbackend.print.dto.response.PriceCalculateResponse;
import com.ilbarslab.ardbackend.print.entity.PriceRule;
import com.ilbarslab.ardbackend.print.entity.ProductType;
import com.ilbarslab.ardbackend.print.repository.PriceRuleRepository;
import com.ilbarslab.ardbackend.print.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final ProductTypeRepository productTypeRepository;
    private final PriceRuleRepository priceRuleRepository;

    public PriceCalculateResponse calculate(PriceCalculateRequest request) {
        ProductType product = productTypeRepository.findBySlug(request.getProductSlug())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + request.getProductSlug()));

        List<PriceRule> rules = priceRuleRepository.findByProductTypeIdOrderByMinQtyAsc(product.getId());

        return switch (product.getPricingModel()) {
            case "AREA_BASED" -> calculateAreaBased(product, rules, request);
            case "PACKAGE" -> calculatePackage(product, rules, request);
            case "TIERED_QUANTITY" -> calculateTiered(product, rules, request);
            case "UNIT" -> calculateUnit(product, rules, request);
            default -> throw new RuntimeException("Bilinmeyen fiyat modeli: " + product.getPricingModel());
        };
    }

    // Alan bazlı: vinil, branda, tabela
    private PriceCalculateResponse calculateAreaBased(ProductType product,
                                                       List<PriceRule> rules,
                                                       PriceCalculateRequest request) {
        if (request.getWidthCm() == null || request.getHeightCm() == null) {
            throw new RuntimeException("Alan bazlı ürünler için en ve boy zorunludur");
        }

        double areaMq = (request.getWidthCm() * request.getHeightCm()) / 10000.0;
        if (areaMq < 0.1) areaMq = 0.1; // minimum alan

        PriceRule baseRule = rules.stream()
                .filter(r -> "AREA_BASED".equals(r.getRuleType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fiyat kuralı bulunamadı"));

        BigDecimal basePrice = baseRule.getBasePrice();
        BigDecimal multiplier = BigDecimal.ONE;

        // Ek seçenek çarpanları (çift yüz, laminasyon vb.)
        if (request.getOptions() != null) {
            multiplier = applyOptionMultipliers(rules, request.getOptions(), multiplier);
        }

        BigDecimal areaBD = BigDecimal.valueOf(areaMq).setScale(4, RoundingMode.HALF_UP);
        BigDecimal unitPrice = basePrice.multiply(areaBD).multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())).setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format("%.2f m² × ₺%.2f/m² × %d adet", areaMq, basePrice, request.getQuantity());

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .priceBreakdown(breakdown)
                .widthCm(request.getWidthCm())
                .heightCm(request.getHeightCm())
                .areaMq(areaMq)
                .build();
    }

    // Paket bazlı: kartvizit, broşür
    private PriceCalculateResponse calculatePackage(ProductType product,
                                                     List<PriceRule> rules,
                                                     PriceCalculateRequest request) {
        PriceRule matchedRule = rules.stream()
                .filter(r -> "PACKAGE".equals(r.getRuleType()))
                .filter(r -> r.getMinQty() != null && r.getMinQty() <= request.getQuantity())
                .filter(r -> r.getMaxQty() == null || r.getMaxQty() >= request.getQuantity())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Bu adet için paket bulunamadı"));

        BigDecimal basePrice = matchedRule.getUnitPrice();
        BigDecimal delta = BigDecimal.ZERO;

        if (request.getOptions() != null) {
            delta = applyOptionDeltas(rules, request.getOptions());
        }

        BigDecimal totalPrice = basePrice.add(delta).setScale(2, RoundingMode.HALF_UP);
        BigDecimal unitPrice = totalPrice.divide(BigDecimal.valueOf(request.getQuantity()), 4, RoundingMode.HALF_UP);

        String breakdown = String.format("%d adet paket: ₺%.2f", request.getQuantity(), basePrice);

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .priceBreakdown(breakdown)
                .build();
    }

    // Kırılımlı adet: sticker, etiket
    private PriceCalculateResponse calculateTiered(ProductType product,
                                                    List<PriceRule> rules,
                                                    PriceCalculateRequest request) {
        PriceRule matchedRule = rules.stream()
                .filter(r -> "TIERED_QUANTITY".equals(r.getRuleType()))
                .filter(r -> r.getMinQty() != null && r.getMinQty() <= request.getQuantity())
                .filter(r -> r.getMaxQty() == null || r.getMaxQty() >= request.getQuantity())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Bu adet için fiyat kırılımı bulunamadı"));

        BigDecimal unitPrice = matchedRule.getUnitPrice();
        BigDecimal multiplier = BigDecimal.ONE;

        if (request.getOptions() != null) {
            multiplier = applyOptionMultipliers(rules, request.getOptions(), multiplier);
        }

        BigDecimal effectiveUnit = unitPrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPrice = effectiveUnit.multiply(BigDecimal.valueOf(request.getQuantity())).setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format("%d adet × ₺%.2f/adet", request.getQuantity(), effectiveUnit);

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(effectiveUnit)
                .totalPrice(totalPrice)
                .priceBreakdown(breakdown)
                .build();
    }

    // Birim bazlı: kupa, kalem, promosyon
    private PriceCalculateResponse calculateUnit(ProductType product,
                                                  List<PriceRule> rules,
                                                  PriceCalculateRequest request) {
        PriceRule baseRule = rules.stream()
                .filter(r -> "UNIT".equals(r.getRuleType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fiyat kuralı bulunamadı"));

        BigDecimal unitPrice = baseRule.getUnitPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())).setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format("%d adet × ₺%.2f/adet", request.getQuantity(), unitPrice);

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .priceBreakdown(breakdown)
                .build();
    }

    private BigDecimal applyOptionMultipliers(List<PriceRule> rules,
                                              Map<String, String> options,
                                              BigDecimal current) {
        for (Map.Entry<String, String> entry : options.entrySet()) {
            for (PriceRule rule : rules) {
                if (entry.getKey().equals(rule.getOptionKey())
                        && entry.getValue().equals(rule.getOptionValue())
                        && rule.getMultiplier() != null) {
                    current = current.multiply(rule.getMultiplier());
                }
            }
        }
        return current;
    }

    private BigDecimal applyOptionDeltas(List<PriceRule> rules, Map<String, String> options) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, String> entry : options.entrySet()) {
            for (PriceRule rule : rules) {
                if (entry.getKey().equals(rule.getOptionKey())
                        && entry.getValue().equals(rule.getOptionValue())
                        && rule.getPriceDelta() != null) {
                    total = total.add(rule.getPriceDelta());
                }
            }
        }
        return total;
    }
}
