package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.UserAppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAppRoleRepository extends JpaRepository<UserAppRole, UUID> {
    List<UserAppRole> findByUserId(UUID userId);
    Optional<UserAppRole> findByUserIdAndAppRoleId(UUID userId, UUID appRoleId);
    void deleteByUserIdAndAppRoleId(UUID userId, UUID appRoleId);
}