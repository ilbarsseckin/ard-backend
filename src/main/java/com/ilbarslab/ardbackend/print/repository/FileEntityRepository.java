package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.FileEntity;
import com.ilbarslab.ardbackend.print.entity.enums.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, UUID> {
    Optional<FileEntity> findByOrderItemId(UUID orderItemId);
    List<FileEntity> findByStatus(FileStatus status);
}
