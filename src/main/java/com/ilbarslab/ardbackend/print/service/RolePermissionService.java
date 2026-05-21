package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.AssignRoleRequest;
import com.ilbarslab.ardbackend.print.dto.request.CreateRoleRequest;
import com.ilbarslab.ardbackend.print.dto.response.AppRoleResponse;
import com.ilbarslab.ardbackend.print.dto.response.PermissionResponse;
import com.ilbarslab.ardbackend.print.entity.AppRole;
import com.ilbarslab.ardbackend.print.entity.Permission;
import com.ilbarslab.ardbackend.print.entity.User;
import com.ilbarslab.ardbackend.print.entity.UserAppRole;
import com.ilbarslab.ardbackend.print.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionService {

    private final AppRoleRepository appRoleRepository;
    private final PermissionRepository permissionRepository;
    private final UserAppRoleRepository userAppRoleRepository;
    private final UserRepository userRepository;

    // Tüm izinleri listele
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    // Tüm rolleri listele
    public List<AppRoleResponse> getAllRoles() {
        return appRoleRepository.findAll().stream()
                .map(this::toRoleResponse)
                .toList();
    }

    // Rol oluştur
    @Transactional
    public AppRoleResponse createRole(CreateRoleRequest request) {
        if (appRoleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Bu isimde bir rol zaten var: " + request.getName());
        }

        Set<Permission> permissions = new HashSet<>();
        if (request.getPermissionIds() != null) {
            permissions = request.getPermissionIds().stream()
                    .map(id -> permissionRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("İzin bulunamadı: " + id)))
                    .collect(Collectors.toSet());
        }

        AppRole role = AppRole.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .isActive(true)
                .build();

        return toRoleResponse(appRoleRepository.save(role));
    }

    // Role izin ekle/çıkar
    @Transactional
    public AppRoleResponse updateRolePermissions(UUID roleId, Set<UUID> permissionIds) {
        AppRole role = appRoleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı"));

        Set<Permission> permissions = permissionIds.stream()
                .map(id -> permissionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("İzin bulunamadı: " + id)))
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        return toRoleResponse(appRoleRepository.save(role));
    }

    // Rolü aktif/pasif yap
    @Transactional
    public void toggleRole(UUID roleId) {
        AppRole role = appRoleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı"));
        role.setIsActive(!role.getIsActive());
        appRoleRepository.save(role);
    }

    // Kullanıcıya rol ata
    @Transactional
    public void assignRole(AssignRoleRequest request, String assignedByEmail) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        AppRole role = appRoleRepository.findById(request.getAppRoleId())
                .orElseThrow(() -> new RuntimeException("Rol bulunamadı"));

        // Zaten atanmış mı kontrol et
        if (userAppRoleRepository.findByUserIdAndAppRoleId(
                request.getUserId(), request.getAppRoleId()).isPresent()) {
            throw new RuntimeException("Bu rol kullanıcıya zaten atanmış");
        }

        UserAppRole userAppRole = UserAppRole.builder()
                .user(user)
                .appRole(role)
                .assignedBy(assignedByEmail)
                .build();

        userAppRoleRepository.save(userAppRole);
        log.info("Rol atandı: {} → {}", user.getEmail(), role.getName());
    }

    // Kullanıcıdan rol kaldır
    @Transactional
    public void removeRole(UUID userId, UUID appRoleId) {
        userAppRoleRepository.deleteByUserIdAndAppRoleId(userId, appRoleId);
        log.info("Rol kaldırıldı: userId={}, roleId={}", userId, appRoleId);
    }

    // Kullanıcının rollerini getir
    public List<AppRoleResponse> getUserRoles(UUID userId) {
        return userAppRoleRepository.findByUserId(userId).stream()
                .map(uar -> toRoleResponse(uar.getAppRole()))
                .toList();
    }

    // Kullanıcının izinlerini getir
    public Set<String> getUserPermissions(UUID userId) {
        return userAppRoleRepository.findByUserId(userId).stream()
                .flatMap(uar -> uar.getAppRole().getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    // Kullanıcının belirli bir izni var mı
    public boolean hasPermission(UUID userId, String permissionCode) {
        Set<String> permissions = getUserPermissions(userId);
        return permissions.contains(permissionCode);
    }

    private AppRoleResponse toRoleResponse(AppRole role) {
        return AppRoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .isActive(role.getIsActive())
                .permissions(role.getPermissions().stream()
                        .map(this::toPermissionResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    private PermissionResponse toPermissionResponse(Permission p) {
        return PermissionResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .label(p.getLabel())
                .category(p.getCategory())
                .build();
    }
}