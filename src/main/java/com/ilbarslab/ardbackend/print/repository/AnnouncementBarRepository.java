package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.AnnouncementBar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnnouncementBarRepository extends JpaRepository<AnnouncementBar, UUID> {
    List<AnnouncementBar> findByActiveTrueOrderBySortOrderAsc();
    List<AnnouncementBar> findAllByOrderBySortOrderAsc();
}
