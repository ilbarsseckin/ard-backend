package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.BulkPriceUpdateRequest;
import com.ilbarslab.ardbackend.print.dto.response.ImportResultResponse;
import com.ilbarslab.ardbackend.print.entity.PriceRule;
import com.ilbarslab.ardbackend.print.entity.ProductType;
import com.ilbarslab.ardbackend.print.repository.PriceRuleRepository;
import com.ilbarslab.ardbackend.print.repository.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImportService {

    private final ProductTypeRepository productTypeRepository;
    private final PriceRuleRepository priceRuleRepository;

    public ImportResultResponse importFromFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new RuntimeException("Dosya adı okunamadı");

        if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return importFromExcel(file);
        } else if (filename.endsWith(".csv")) {
            return importFromCsv(file);
        } else {
            throw new RuntimeException("Desteklenmeyen format. Sadece .xlsx veya .csv yükleyebilirsiniz.");
        }
    }

    private ImportResultResponse importFromExcel(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim().toLowerCase());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    rowData.put(headers.get(j), getCellValue(cell));
                }
                rows.add(rowData);
            }
        }

        return processRows(rows);
    }

    private ImportResultResponse importFromCsv(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) throw new RuntimeException("CSV dosyası boş");

            String[] headers = headerLine.split(",");
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim().toLowerCase().replace("\"", "");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, String> rowData = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String val = i < values.length ? values[i].trim().replace("\"", "") : "";
                    rowData.put(headers[i], val);
                }
                rows.add(rowData);
            }
        }

        return processRows(rows);
    }

    private ImportResultResponse processRows(List<Map<String, String>> rows) {
        int imported = 0, updated = 0, warnings = 0, errors = 0;
        List<String> errorMessages = new ArrayList<>();
        List<String> warningMessages = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            int rowNum = i + 2;

            try {
                // Zorunlu alanlar
                String name = row.get("urun_adi");
                String category = row.get("kategori");
                String unit = row.get("birim");
                String priceStr = row.get("liste_fiyati");

                if (isBlank(name)) { errors++; errorMessages.add("Satır " + rowNum + ": urun_adi boş"); continue; }
                if (isBlank(category)) { errors++; errorMessages.add("Satır " + rowNum + ": kategori boş"); continue; }
                if (isBlank(priceStr)) { errors++; errorMessages.add("Satır " + rowNum + ": liste_fiyati boş"); continue; }

                BigDecimal price;
                try {
                    price = new BigDecimal(priceStr.replace(",", ".").trim());
                } catch (Exception e) {
                    errors++;
                    errorMessages.add("Satır " + rowNum + ": Geçersiz fiyat: " + priceStr);
                    continue;
                }

                // Opsiyonel alanlar
                String minOrderStr = row.getOrDefault("min_adet", "1");
                int minOrder = 1;
                try { minOrder = Integer.parseInt(minOrderStr.trim()); } catch (Exception ignored) {}

                String description = row.getOrDefault("aciklama", "");
                String activeStr = row.getOrDefault("aktif", "1");
                boolean isActive = !"0".equals(activeStr.trim());

                // Slug oluştur
                String slug = category.toLowerCase().trim()
                        .replace(" ", "-")
                        .replace("ı", "i").replace("ğ", "g")
                        .replace("ü", "u").replace("ş", "s")
                        .replace("ö", "o").replace("ç", "c")
                        + "-" + name.toLowerCase().trim()
                        .replace(" ", "-")
                        .replace("ı", "i").replace("ğ", "g")
                        .replace("ü", "u").replace("ş", "s")
                        .replace("ö", "o").replace("ç", "c");

                // Pricing model belirle
                String pricingModel = determinePricingModel(category, unit);
                String unitNorm = normalizeUnit(unit);

                // Eksik alan uyarıları
                if (isBlank(unit)) {
                    warnings++;
                    warningMessages.add("Satır " + rowNum + ": birim eksik, 'adet' varsayıldı");
                }

                // Kaydet veya güncelle
                Optional<ProductType> existing = productTypeRepository.findBySlug(slug);
                ProductType productType;

                if (existing.isPresent()) {
                    productType = existing.get();
                    productType.setName(name);
                    productType.setPricingModel(pricingModel);
                    productType.setUnit(unitNorm);
                    productType.setMinOrder(minOrder);
                    productType.setIsActive(isActive);
                    productType.setDescription(description);
                    updated++;
                } else {
                    productType = ProductType.builder()
                            .name(name)
                            .slug(slug)
                            .pricingModel(pricingModel)
                            .unit(unitNorm)
                            .hasFile(!"promosyon".equalsIgnoreCase(category))
                            .minOrder(minOrder)
                            .isActive(isActive)
                            .description(description)
                            .build();
                    imported++;
                }

                productTypeRepository.save(productType);

                // Temel fiyat kuralı oluştur/güncelle
                List<PriceRule> existingRules = priceRuleRepository.findByProductTypeIdOrderByMinQtyAsc(productType.getId());
                if (existingRules.isEmpty()) {
                    PriceRule rule = PriceRule.builder()
                            .productType(productType)
                            .ruleType(pricingModel)
                            .basePrice("AREA_BASED".equals(pricingModel) ? price : null)
                            .unitPrice(!"AREA_BASED".equals(pricingModel) ? price : null)
                            .minQty(minOrder)
                            .build();
                    priceRuleRepository.save(rule);
                }

            } catch (Exception e) {
                errors++;
                errorMessages.add("Satır " + rowNum + ": " + e.getMessage());
                log.error("Import hatası satır {}: {}", rowNum, e.getMessage());
            }
        }

        return ImportResultResponse.builder()
                .totalRows(rows.size())
                .imported(imported)
                .updated(updated)
                .warnings(warnings)
                .errors(errors)
                .errorMessages(errorMessages)
                .warningMessages(warningMessages)
                .build();
    }

    private String determinePricingModel(String category, String unit) {
        if (category == null) return "UNIT";
        String cat = category.toLowerCase();
        if (cat.contains("vinil") || cat.contains("branda") || cat.contains("tabela") || cat.contains("buyuk-format") || "m2".equalsIgnoreCase(unit)) return "AREA_BASED";
        if (cat.contains("kartvizit") || cat.contains("brosur") || cat.contains("flyer")) return "PACKAGE";
        if (cat.contains("sticker") || cat.contains("etiket")) return "TIERED_QUANTITY";
        return "UNIT";
    }

    private String normalizeUnit(String unit) {
        if (unit == null || unit.isBlank()) return "adet";
        return switch (unit.toLowerCase().trim()) {
            case "m2", "m²", "metrekare" -> "m2";
            case "paket", "pkg" -> "paket";
            default -> "adet";
        };
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val)) yield String.valueOf((long) val);
                yield String.valueOf(val);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // Toplu fiyat güncelleme
    public int bulkUpdatePrice(BulkPriceUpdateRequest request) {
        List<ProductType> products;

        if (request.getCategorySlug() != null && !request.getCategorySlug().isBlank()) {
            products = productTypeRepository.findAll().stream()
                    .filter(p -> p.getSlug().startsWith(request.getCategorySlug()))
                    .toList();
        } else {
            products = productTypeRepository.findAll();
        }

        int updated = 0;
        for (ProductType product : products) {
            List<PriceRule> rules = priceRuleRepository.findByProductTypeIdOrderByMinQtyAsc(product.getId());
            for (PriceRule rule : rules) {
                BigDecimal currentPrice = rule.getBasePrice() != null ? rule.getBasePrice() : rule.getUnitPrice();
                if (currentPrice == null) continue;

                BigDecimal newPrice = switch (request.getUpdateType()) {
                    case "PERCENT_INCREASE" -> currentPrice.multiply(BigDecimal.valueOf(1 + request.getValue() / 100));
                    case "PERCENT_DECREASE" -> currentPrice.multiply(BigDecimal.valueOf(1 - request.getValue() / 100));
                    case "FIXED_INCREASE" -> currentPrice.add(BigDecimal.valueOf(request.getValue()));
                    case "FIXED_PRICE" -> BigDecimal.valueOf(request.getValue());
                    default -> currentPrice;
                };

                newPrice = newPrice.setScale(2, java.math.RoundingMode.HALF_UP);

                if (rule.getBasePrice() != null) rule.setBasePrice(newPrice);
                else rule.setUnitPrice(newPrice);

                priceRuleRepository.save(rule);
                updated++;
            }
        }
        return updated;
    }
}
