package com.ilbarslab.ardbackend.print.security;

import com.ilbarslab.ardbackend.print.repository.UserAppRoleRepository;
import com.ilbarslab.ardbackend.print.repository.UserRepository;
import com.ilbarslab.ardbackend.print.entity.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("perm")
@RequiredArgsConstructor
public class PermissionEvaluator {

    private final UserRepository userRepository;
    private final UserAppRoleRepository userAppRoleRepository;

    public boolean has(Authentication auth, String permissionCode) {
        if (auth == null || !auth.isAuthenticated()) return false;

        String email = auth.getName();

        // Admin her şeyi yapabilir
        return userRepository.findByEmail(email).map(user -> {
            if (user.getRole().name().equals("ADMIN")) return true;

            // Özel rol izinlerini kontrol et
            Set<String> perms = userAppRoleRepository.findByUserId(user.getId())
                    .stream()
                    .flatMap(uar -> uar.getAppRole().getPermissions().stream())
                    .map(Permission::getCode)
                    .collect(Collectors.toSet());

            return perms.contains(permissionCode);
        }).orElse(false);
    }
}