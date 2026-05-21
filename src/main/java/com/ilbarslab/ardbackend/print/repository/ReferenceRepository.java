package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReferenceRepository extends JpaRepository<Reference, UUID> {
    List<Reference> findByActiveOrderByDisplayOrderAsc(Boolean active);
    List<Reference> findByCategoryAndActiveOrderByDisplayOrderAsc(String category, Boolean active);
    List<Reference> findByFeaturedAndActiveOrderByDisplayOrderAsc(Boolean featured, Boolean active);
}
