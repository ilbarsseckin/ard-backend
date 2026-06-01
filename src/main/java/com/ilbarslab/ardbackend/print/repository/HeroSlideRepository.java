package com.ilbarslab.ardbackend.print.repository;

import com.ilbarslab.ardbackend.print.entity.HeroSlide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HeroSlideRepository extends JpaRepository<HeroSlide, UUID> {

    /** Admin için — tüm slide'lar sıralı */
    List<HeroSlide> findAllByOrderBySortOrderAscCreatedAtAsc();

    /** Public için — sadece aktif slide'lar (tarih kontrolü service'te) */
    List<HeroSlide> findByActiveTrueOrderBySortOrderAscCreatedAtAsc();
}
