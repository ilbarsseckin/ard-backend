package com.ilbarslab.ardbackend.print.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilbarslab.ardbackend.print.dto.response.AuthResponse;
import com.ilbarslab.ardbackend.print.entity.User;
import com.ilbarslab.ardbackend.print.entity.enums.Role;
import com.ilbarslab.ardbackend.print.repository.UserRepository;
import com.ilbarslab.ardbackend.print.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    public AuthResponse loginWithGoogle(String code, String redirectUri) {
        String accessToken = exchangeCodeForToken(code, redirectUri);
        GoogleUserInfo userInfo = fetchUserInfo(accessToken);
        log.info("Google giriş: {}", userInfo.email());

        User user = userRepository.findByEmail(userInfo.email())
                .map(existing -> {
                    if (existing.getGoogleId() == null) {
                        existing.setGoogleId(userInfo.googleId());
                        existing.setEmailVerified(true);
                        userRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name(userInfo.name())
                            .email(userInfo.email())
                            .googleId(userInfo.googleId())
                            .emailVerified(true)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .role(Role.CUSTOMER)
                            .build();
                    return userRepository.save(newUser);
                });

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .id(user.getId().toString())
                .build();
    }

    private String exchangeCodeForToken(String code, String redirectUri) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            String accessToken = json.get("access_token").asText();
            if (accessToken == null || accessToken.isEmpty()) {
                throw new RuntimeException("Google'dan access token alınamadı");
            }
            return accessToken;
        } catch (Exception e) {
            log.error("Google token exchange hatası: {}", e.getMessage());
            throw new RuntimeException("Google giriş token hatası: " + e.getMessage());
        }
    }

    private GoogleUserInfo fetchUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v3/userinfo",
                HttpMethod.GET, request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return new GoogleUserInfo(
                    json.get("sub").asText(),
                    json.get("email").asText(),
                    json.path("name").asText("Kullanıcı")
            );
        } catch (Exception e) {
            log.error("Google userinfo hatası: {}", e.getMessage());
            throw new RuntimeException("Google kullanıcı bilgisi alınamadı");
        }
    }

    public record GoogleUserInfo(String googleId, String email, String name) {}
}