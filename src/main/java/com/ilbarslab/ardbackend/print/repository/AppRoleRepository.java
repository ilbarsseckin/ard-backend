package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, UUID> {
    Optional<AppRole> findByName(String name);
    List<AppRole> findByIsActiveTrue();
}