package com.ilbarslab.ardbackend.print.service;

import com.iyzipay.Options;
import com.iyzipay.model.InstallmentDetail;
import com.iyzipay.model.InstallmentInfo;
import com.iyzipay.model.InstallmentPrice;
import com.iyzipay.model.Locale;
import com.iyzipay.request.RetrieveInstallmentInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class InstallmentService {

    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    private Options getOptions() {
        Options options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);
        return options;
    }

    public List<Map<String, Object>> getInstallments(String binNumber, BigDecimal price) {
        try {
            RetrieveInstallmentInfoRequest request = new RetrieveInstallmentInfoRequest();
            request.setLocale(Locale.TR.getValue());
            request.setBinNumber(binNumber.replaceAll("\\D", "").substring(0, Math.min(6, binNumber.length())));
            request.setPrice(price);

            InstallmentInfo info = InstallmentInfo.retrieve(request, getOptions());

            if (!"success".equalsIgnoreCase(info.getStatus())) {
                log.warn("iyzico taksit sorgu hata: {}", info.getErrorMessage());
                return defaultInstallments(price);
            }

            List<Map<String, Object>> result = new ArrayList<>();

            if (info.getInstallmentDetails() == null) return defaultInstallments(price);

            for (InstallmentDetail detail : info.getInstallmentDetails()) {
                if (detail.getInstallmentPrices() == null) continue;
                for (InstallmentPrice ip : detail.getInstallmentPrices()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    int count = ip.getInstallmentNumber();
                    BigDecimal totalPrice = ip.getTotalPrice() != null ? ip.getTotalPrice() : price;
                    BigDecimal monthlyPrice = ip.getInstallmentPrice() != null
                        ? ip.getInstallmentPrice()
                        : totalPrice.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

                    row.put("installment", count);
                    row.put("monthlyPrice", monthlyPrice);
                    row.put("totalPrice", totalPrice);
                    row.put("bankName", detail.getBankName() != null ? detail.getBankName() : "");
                    row.put("cardAssociation", detail.getCardAssociation() != null ? detail.getCardAssociation() : "");
                    row.put("cardFamilyName", detail.getCardFamilyName() != null ? detail.getCardFamilyName() : "");
                    result.add(row);
                }
            }

            // Eğer boşsa default döndür
            return result.isEmpty() ? defaultInstallments(price) : result;

        } catch (Exception e) {
            log.error("Taksit sorgulama hatası: {}", e.getMessage());
            return defaultInstallments(price);
        }
    }

    /** BIN sorgusu başarısız olursa gösterilecek varsayılan taksitler */
    private List<Map<String, Object>> defaultInstallments(BigDecimal price) {
        List<Map<String, Object>> result = new ArrayList<>();
        int[] counts = {1, 2, 3, 6, 9, 12};
        for (int count : counts) {
            BigDecimal monthly = price.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("installment", count);
            row.put("monthlyPrice", monthly);
            row.put("totalPrice", price);
            row.put("bankName", "");
            row.put("cardAssociation", "");
            row.put("cardFamilyName", "");
            result.add(row);
        }
        return result;
    }
}
