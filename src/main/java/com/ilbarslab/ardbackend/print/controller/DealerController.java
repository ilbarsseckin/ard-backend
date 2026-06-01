package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.DealerApplicationRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.entity.Dealer;
import com.ilbarslab.ardbackend.print.entity.User;
import com.ilbarslab.ardbackend.print.entity.enums.DealerStatus;
import com.ilbarslab.ardbackend.print.entity.enums.Role;
import com.ilbarslab.ardbackend.print.repository.DealerRepository;
import com.ilbarslab.ardbackend.print.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DealerController {

    private final DealerRepository dealerRepository;
    private final UserRepository userRepository;

    // Herkese açık — başvuru formu gönder
    @PostMapping("/api/dealer/apply")
    public ResponseEntity<ApiResponse<String>> apply(
            @RequestBody DealerApplicationRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        if (dealerRepository.findByUserId(user.getId()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Zaten bir başvurunuz bulunuyor"));
        }
        if (dealerRepository.existsByTaxNumber(req.getTaxNumber())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Bu vergi numarası zaten kayıtlı"));
        }

        Dealer dealer = Dealer.builder()
                .user(user)
                .companyName(req.getCompanyName())
                .taxNumber(req.getTaxNumber())
                .taxOffice(req.getTaxOffice())
                .phone(req.getPhone())
                .address(req.getAddress())
                .city(req.getCity())
                .district(req.getDistrict())
                .status(DealerStatus.PENDING)
                .build();

        dealerRepository.save(dealer);
        return ResponseEntity.ok(ApiResponse.ok("Başvurunuz alındı, inceleme sürecindeyiz", "OK"));
    }

    // Kendi bayi bilgilerini gör
    @GetMapping("/api/dealer/me")
    public ResponseEntity<ApiResponse<Dealer>> getMe(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Dealer dealer = dealerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Bayi kaydı bulunamadı"));
        return ResponseEntity.ok(ApiResponse.ok(null, dealer));
    }

    // ADMIN — tüm başvurular
    @GetMapping("/api/admin/dealers")
    public ResponseEntity<ApiResponse<List<Dealer>>> getAll(
            @RequestParam(required = false) DealerStatus status) {
        List<Dealer> dealers = status != null
                ? dealerRepository.findByStatus(status)
                : dealerRepository.findAll();
        return ResponseEntity.ok(ApiResponse.ok(null, dealers));
    }

    // ADMIN — onayla
    @PostMapping("/api/admin/dealers/{id}/approve")
    public ResponseEntity<ApiResponse<String>> approve(
            @PathVariable UUID id,
            @RequestBody(required = false) Map<String, Object> body) {

        Dealer dealer = dealerRepository.findById(id).orElseThrow();
        dealer.setStatus(DealerStatus.APPROVED);

        if (body != null) {
            if (body.containsKey("discountRate"))
                dealer.setDiscountRate(new BigDecimal(body.get("discountRate").toString()));
            if (body.containsKey("creditLimit"))
                dealer.setCreditLimit(new BigDecimal(body.get("creditLimit").toString()));
            if (body.containsKey("notes"))
                dealer.setNotes(body.get("notes").toString());
        }

        // Kullanıcıya DEALER rolü ver
        User user = dealer.getUser();
        user.setRole(Role.DEALER);
        userRepository.save(user);
        dealerRepository.save(dealer);

        return ResponseEntity.ok(ApiResponse.ok("Bayi onaylandı", "OK"));
    }

    // ADMIN — reddet
    @PostMapping("/api/admin/dealers/{id}/reject")
    public ResponseEntity<ApiResponse<String>> reject(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        Dealer dealer = dealerRepository.findById(id).orElseThrow();
        dealer.setStatus(DealerStatus.REJECTED);
        dealer.setRejectionReason(body.getOrDefault("reason", ""));
        dealerRepository.save(dealer);
        return ResponseEntity.ok(ApiResponse.ok("Başvuru reddedildi", "OK"));
    }

    // ADMIN — iskonto/limit güncelle
    @PatchMapping("/api/admin/dealers/{id}/settings")
    public ResponseEntity<ApiResponse<String>> updateSettings(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        Dealer dealer = dealerRepository.findById(id).orElseThrow();
        if (body.containsKey("discountRate"))
            dealer.setDiscountRate(new BigDecimal(body.get("discountRate").toString()));
        if (body.containsKey("creditLimit"))
            dealer.setCreditLimit(new BigDecimal(body.get("creditLimit").toString()));
        if (body.containsKey("notes"))
            dealer.setNotes(body.get("notes").toString());
        dealerRepository.save(dealer);
        return ResponseEntity.ok(ApiResponse.ok("Güncellendi", "OK"));
    }
}