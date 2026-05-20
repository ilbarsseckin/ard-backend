package com.ilbarslab.ardbackend.print.config;

import com.ilbarslab.ardbackend.print.entity.PriceRule;
import com.ilbarslab.ardbackend.print.entity.ProductType;
import com.ilbarslab.ardbackend.print.repository.PriceRuleRepository;
import com.ilbarslab.ardbackend.print.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ProductTypeRepository productTypeRepository;
    private final PriceRuleRepository priceRuleRepository;

    @Override
    public void run(String... args) {
        if (productTypeRepository.count() > 0) {
            log.info("Ürün verileri zaten mevcut, başlangıç verisi yüklenmedi.");
            return;
        }

        log.info("Başlangıç ürün verileri yükleniyor...");

        // Vinil Baskı
        ProductType vinil = save(ProductType.builder()
                .name("Vinil Baskı").slug("buyuk-format-vinil")
                .pricingModel("AREA_BASED").unit("m2")
                .hasFile(true).minOrder(1).isActive(true)
                .description("Yüksek kalite vinil baskı").build());
        saveRule(PriceRule.builder().productType(vinil).ruleType("AREA_BASED")
                .basePrice(new BigDecimal("185.00")).build());
        saveRule(PriceRule.builder().productType(vinil).ruleType("OPTION")
                .optionKey("doublesided").optionValue("true")
                .multiplier(new BigDecimal("1.6")).build());

        // Branda
        ProductType branda = save(ProductType.builder()
                .name("Branda Baskı").slug("buyuk-format-branda")
                .pricingModel("AREA_BASED").unit("m2")
                .hasFile(true).minOrder(1).isActive(true)
                .description("Dış mekan branda baskı").build());
        saveRule(PriceRule.builder().productType(branda).ruleType("AREA_BASED")
                .basePrice(new BigDecimal("120.00")).build());

        // Kartvizit
        ProductType kartvizit = save(ProductType.builder()
                .name("Kartvizit 350g").slug("kartvizit-350g")
                .pricingModel("PACKAGE").unit("paket")
                .hasFile(true).minOrder(250).isActive(true)
                .description("350gr kartvizit baskı").build());
        saveRule(PriceRule.builder().productType(kartvizit).ruleType("PACKAGE")
                .minQty(250).maxQty(499).unitPrice(new BigDecimal("180.00")).build());
        saveRule(PriceRule.builder().productType(kartvizit).ruleType("PACKAGE")
                .minQty(500).maxQty(999).unitPrice(new BigDecimal("280.00")).build());
        saveRule(PriceRule.builder().productType(kartvizit).ruleType("PACKAGE")
                .minQty(1000).maxQty(null).unitPrice(new BigDecimal("420.00")).build());
        saveRule(PriceRule.builder().productType(kartvizit).ruleType("OPTION")
                .optionKey("coating").optionValue("selofan")
                .priceDelta(new BigDecimal("40.00")).build());
        saveRule(PriceRule.builder().productType(kartvizit).ruleType("OPTION")
                .optionKey("doublesided").optionValue("true")
                .priceDelta(new BigDecimal("60.00")).build());

        // Sticker
        ProductType sticker = save(ProductType.builder()
                .name("Sticker Baskı").slug("sticker-genel")
                .pricingModel("TIERED_QUANTITY").unit("adet")
                .hasFile(true).minOrder(10).isActive(true)
                .description("Kesim sticker ve etiket").build());
        saveRule(PriceRule.builder().productType(sticker).ruleType("TIERED_QUANTITY")
                .minQty(10).maxQty(50).unitPrice(new BigDecimal("8.00")).build());
        saveRule(PriceRule.builder().productType(sticker).ruleType("TIERED_QUANTITY")
                .minQty(51).maxQty(200).unitPrice(new BigDecimal("5.00")).build());
        saveRule(PriceRule.builder().productType(sticker).ruleType("TIERED_QUANTITY")
                .minQty(201).maxQty(500).unitPrice(new BigDecimal("3.50")).build());
        saveRule(PriceRule.builder().productType(sticker).ruleType("TIERED_QUANTITY")
                .minQty(501).maxQty(null).unitPrice(new BigDecimal("2.50")).build());
        saveRule(PriceRule.builder().productType(sticker).ruleType("OPTION")
                .optionKey("cutting").optionValue("custom")
                .multiplier(new BigDecimal("1.30")).build());

        // Forex Tabela
        ProductType forex = save(ProductType.builder()
                .name("Forex Tabela").slug("tabela-forex")
                .pricingModel("AREA_BASED").unit("m2")
                .hasFile(true).minOrder(1).isActive(true)
                .description("5mm forex tabela baskı").build());
        saveRule(PriceRule.builder().productType(forex).ruleType("AREA_BASED")
                .basePrice(new BigDecimal("120.00")).build());

        // Kupa
        ProductType kupa = save(ProductType.builder()
                .name("Kupa Baskı").slug("promosyon-kupa")
                .pricingModel("UNIT").unit("adet")
                .hasFile(false).minOrder(1).isActive(true)
                .description("Beyaz kupa baskı").build());
        saveRule(PriceRule.builder().productType(kupa).ruleType("UNIT")
                .unitPrice(new BigDecimal("45.00")).build());

        // Bez Çanta
        ProductType canta = save(ProductType.builder()
                .name("Bez Çanta Baskı").slug("promosyon-bez-canta")
                .pricingModel("UNIT").unit("adet")
                .hasFile(false).minOrder(10).isActive(true)
                .description("Bez çanta tek taraf baskı").build());
        saveRule(PriceRule.builder().productType(canta).ruleType("UNIT")
                .unitPrice(new BigDecimal("35.00")).build());

        log.info("Başlangıç verileri yüklendi: {} ürün", productTypeRepository.count());
    }

    private ProductType save(ProductType pt) {
        return productTypeRepository.save(pt);
    }

    private void saveRule(PriceRule rule) {
        priceRuleRepository.save(rule);
    }
}
