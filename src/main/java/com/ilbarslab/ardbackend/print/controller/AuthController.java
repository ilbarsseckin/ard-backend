package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.LoginRequest;
import com.ilbarslab.ardbackend.print.dto.request.RegisterRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.AuthResponse;
import com.ilbarslab.ardbackend.print.service.AuthService;
import com.ilbarslab.ardbackend.print.service.GoogleAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ilbarslab.ardbackend.print.dto.request.GoogleAuthRequest;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    // sınıf alanına ekle:
    private final GoogleAuthService googleAuthService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Kayıt başarılı", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Giriş başarılı", response));
    }


    // import ekle:


    // metod ekle:
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(
            @RequestBody GoogleAuthRequest request) {
        AuthResponse response = googleAuthService.loginWithGoogle(
                request.getCode(),
                request.getRedirectUri()
        );
        return ResponseEntity.ok(ApiResponse.ok("Google girişi başarılı", response));
    }
}