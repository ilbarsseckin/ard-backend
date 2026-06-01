package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.PriceCalculateRequest;
import com.ilbarslab.ardbackend.print.dto.response.PriceCalculateResponse;
import com.ilbarslab.ardbackend.print.entity.PriceRule;
import com.ilbarslab.ardbackend.print.entity.ProductType;
import com.ilbarslab.ardbackend.print.repository.PriceRuleRepository;
import com.ilbarslab.ardbackend.print.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final ProductTypeRepository productTypeRepository;
    private final PriceRuleRepository priceRuleRepository;
    private final SystemSettingService settingService;

    public PriceCalculateResponse calculate(PriceCalculateRequest request) {
        ProductType product = productTypeRepository.findBySlug(request.getProductSlug())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ürün bulunamadı: " + request.getProductSlug()));

        List<PriceRule> rules = priceRuleRepository.findByProductTypeIdOrderByMinQtyAsc(product.getId());
        BigDecimal usdRate = getUsdRate();

        return switch (product.getPricingModel()) {
            case "AREA_BASED" -> calculateAreaBased(product, rules, request, usdRate);
            case "PACKAGE" -> calculatePackage(product, rules, request, usdRate);
            case "TIERED_QUANTITY" -> calculateTiered(product, rules, request, usdRate);
            case "UNIT" -> calculateUnit(product, rules, request, usdRate);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bilinmeyen fiyat modeli: " + product.getPricingModel());
        };
    }

    private BigDecimal getUsdRate() {
        Double rate = settingService.getDouble("usd_kur", 45.0);
        return BigDecimal.valueOf(rate);
    }

    // Alan bazlı — basePrice = USD/m²
    private PriceCalculateResponse calculateAreaBased(ProductType product,
                                                      List<PriceRule> rules,
                                                      PriceCalculateRequest request,
                                                      BigDecimal usdRate) {
        if (request.getWidthCm() == null || request.getHeightCm() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Alan bazlı ürünler için en ve boy zorunludur");
        }

        double areaMq = (request.getWidthCm() * request.getHeightCm()) / 10000.0;
        if (areaMq < 0.1) areaMq = 0.1;

        PriceRule baseRule = rules.stream()
                .filter(r -> "AREA_BASED".equals(r.getRuleType()) && r.getOptionKey() == null)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Fiyat kuralı bulunamadı"));

        BigDecimal usdPerM2 = baseRule.getBasePrice();
        BigDecimal multiplier = BigDecimal.ONE;
        if (request.getOptions() != null) {
            multiplier = applyOptionMultipliers(rules, request.getOptions(), multiplier);
        }

        BigDecimal areaBD = BigDecimal.valueOf(areaMq).setScale(4, RoundingMode.HALF_UP);
        // USD birim fiyat = $/m² × m² × multiplier
        BigDecimal usdUnit = usdPerM2.multiply(areaBD).multiply(multiplier);
        // TL'ye çevir
        BigDecimal unitPriceTl = usdUnit.multiply(usdRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPriceTl = unitPriceTl.multiply(BigDecimal.valueOf(request.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format("%.2f m² × $%.2f/m² × %d adet × ₺%.2f kur",
                areaMq, usdPerM2, request.getQuantity(), usdRate);

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPriceTl)
                .totalPrice(totalPriceTl)
                .priceBreakdown(breakdown)
                .widthCm(request.getWidthCm())
                .heightCm(request.getHeightCm())
                .areaMq(areaMq)
                .build();
    }

    // Paket — unitPrice = USD/paket (toplam fiyat)
    private PriceCalculateResponse calculatePackage(ProductType product,
                                                    List<PriceRule> rules,
                                                    PriceCalculateRequest request,
                                                    BigDecimal usdRate) {
        PriceRule matchedRule = rules.stream()
                .filter(r -> "PACKAGE".equals(r.getRuleType()) && r.getOptionKey() == null)
                .filter(r -> r.getMinQty() != null && r.getMinQty() <= request.getQuantity())
                .filter(r -> r.getMaxQty() == null || r.getMaxQty() >= request.getQuantity())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Bu adet için paket bulunamadı"));

        BigDecimal usdPackage = matchedRule.getUnitPrice();
        BigDecimal usdDelta = BigDecimal.ZERO;
        if (request.getOptions() != null) {
            usdDelta = applyOptionDeltas(rules, request.getOptions());
        }

        BigDecimal usdTotal = usdPackage.add(usdDelta);
        BigDecimal totalPriceTl = usdTotal.multiply(usdRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal unitPriceTl = totalPriceTl
                .divide(BigDecimal.valueOf(request.getQuantity()), 4, RoundingMode.HALF_UP);

        String breakdown = String.format("%d adet paket: $%.2f × ₺%.2f kur",
                request.getQuantity(), usdPackage, usdRate);

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPriceTl)
                .totalPrice(totalPriceTl)
                .priceBreakdown(breakdown)
                .build();
    }

    // Kademeli adet — unitPrice = USD/adet (kademeye göre)
    private PriceCalculateResponse calculateTiered(ProductType product,
                                                   List<PriceRule> rules,
                                                   PriceCalculateRequest request,
                                                   BigDecimal usdRate) {
        PriceRule matchedRule = rules.stream()
                .filter(r -> "TIERED_QUANTITY".equals(r.getRuleType()) && r.getOptionKey() == null)
                .filter(r -> r.getMinQty() != null && r.getMinQty() <= request.getQuantity())
                .filter(r -> r.getMaxQty() == null || r.getMaxQty() >= request.getQuantity())
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Bu adet için fiyat baremi bulunamadı"));

        BigDecimal usdUnit = matchedRule.getUnitPrice();
        BigDecimal multiplier = BigDecimal.ONE;
        if (request.getOptions() != null) {
            multiplier = applyOptionMultipliers(rules, request.getOptions(), multiplier);
        }

        BigDecimal usdEffective = usdUnit.multiply(multiplier);
        BigDecimal unitPriceTl = usdEffective.multiply(usdRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPriceTl = unitPriceTl.multiply(BigDecimal.valueOf(request.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format("%d adet × $%.2f/adet × ₺%.2f kur",
                request.getQuantity(), usdEffective, usdRate);

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPriceTl)
                .totalPrice(totalPriceTl)
                .priceBreakdown(breakdown)
                .build();
    }

    // Birim — unitPrice = USD/adet (sabit)
    private PriceCalculateResponse calculateUnit(ProductType product,
                                                 List<PriceRule> rules,
                                                 PriceCalculateRequest request,
                                                 BigDecimal usdRate) {
        PriceRule baseRule = rules.stream()
                .filter(r -> "UNIT".equals(r.getRuleType()) && r.getOptionKey() == null)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Fiyat kuralı bulunamadı"));

        BigDecimal usdUnit = baseRule.getUnitPrice();
        BigDecimal unitPriceTl = usdUnit.multiply(usdRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPriceTl = unitPriceTl.multiply(BigDecimal.valueOf(request.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format("%d adet × $%.2f/adet × ₺%.2f kur",
                request.getQuantity(), usdUnit, usdRate);

        return PriceCalculateResponse.builder()
                .productSlug(product.getSlug())
                .productName(product.getName())
                .quantity(request.getQuantity())
                .unitPrice(unitPriceTl)
                .totalPrice(totalPriceTl)
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