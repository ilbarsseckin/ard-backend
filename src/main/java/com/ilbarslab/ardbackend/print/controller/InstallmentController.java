package com.ilbarslab.ardbackend.print.controller;

import com.iyzipay.Options;
import com.iyzipay.model.InstallmentInfo;
import com.iyzipay.request.RetrieveInstallmentInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/installment")
@Slf4j
public class InstallmentController {

    @Value("${iyzico.api-key}")
    private String apiKey;

    @Value("${iyzico.secret-key}")
    private String secretKey;

    @Value("${iyzico.base-url}")
    private String baseUrl;

    /**
     * İyzico'dan taksit bilgilerini çeker.
     * GET /api/installment?price=1500
     */
    @GetMapping
    public ResponseEntity<?> getInstallments(@RequestParam(defaultValue = "1000") String price) {
        try {
            Options options = new Options();
            options.setApiKey(apiKey);
            options.setSecretKey(secretKey);
            options.setBaseUrl(baseUrl);

            RetrieveInstallmentInfoRequest request = new RetrieveInstallmentInfoRequest();
            request.setLocale("tr");
            request.setPrice(new BigDecimal(price));

            InstallmentInfo info = InstallmentInfo.retrieve(request, options);

            if (!"success".equalsIgnoreCase(info.getStatus())) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", info.getErrorMessage()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", info.getInstallmentDetails()
            ));
        } catch (Exception e) {
            log.error("Taksit bilgisi alınamadı: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
