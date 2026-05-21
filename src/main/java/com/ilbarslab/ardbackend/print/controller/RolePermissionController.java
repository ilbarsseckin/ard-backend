package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.AssignRoleRequest;
import com.ilbarslab.ardbackend.print.dto.request.CreateRoleRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.AppRoleResponse;
import com.ilbarslab.ardbackend.print.dto.response.PermissionResponse;
import com.ilbarslab.ardbackend.print.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.ok(rolePermissionService.getAllPermissions()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AppRoleResponse>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.ok(rolePermissionService.getAllRoles()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppRoleResponse>> createRole(
            @Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Rol oluşturuldu", rolePermissionService.createRole(request)));
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<AppRoleResponse>> updatePermissions(
            @PathVariable UUID roleId,
            @RequestBody Set<UUID> permissionIds) {
        return ResponseEntity.ok(ApiResponse.ok("İzinler güncellendi",
                rolePermissionService.updateRolePermissions(roleId, permissionIds)));
    }

    @PatchMapping("/{roleId}/toggle")
    public ResponseEntity<ApiResponse<Void>> toggleRole(@PathVariable UUID roleId) {
        rolePermissionService.toggleRole(roleId);
        return ResponseEntity.ok(ApiResponse.ok("Rol durumu güncellendi", null));
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<Void>> assignRole(
            @Valid @RequestBody AssignRoleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        rolePermissionService.assignRole(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Rol atandı", null));
    }

    @DeleteMapping("/users/{userId}/roles/{appRoleId}")
    public ResponseEntity<ApiResponse<Void>> removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID appRoleId) {
        rolePermissionService.removeRole(userId, appRoleId);
        return ResponseEntity.ok(ApiResponse.ok("Rol kaldırıldı", null));
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<ApiResponse<List<AppRoleResponse>>> getUserRoles(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(rolePermissionService.getUserRoles(userId)));
    }

    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<ApiResponse<Set<String>>> getUserPermissions(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok(rolePermissionService.getUserPermissions(userId)));
    }
}