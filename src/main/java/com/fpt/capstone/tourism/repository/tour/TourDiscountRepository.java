package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourDiscountRepository extends JpaRepository<TourDiscount, Long> {


    @Query("SELECT td FROM TourDiscount td " +
            "JOIN td.tourSchedule ts " +
            "WHERE td.startDate <= :now AND td.endDate >= :now " +
            "AND td.deleted = false " +
            "AND ts.deleted = false " +
            "AND ts.published = true " +
            "AND ts.departureDate >= :now " +
            "AND ts.tour.tourStatus = com.fpt.capstone.tourism.model.enums.TourStatus.PUBLISHED " +
            "AND ts.tour.deleted = false " +
            "ORDER BY td.discountPercent DESC")
    List<TourDiscount> findTopDiscountedTours(@Param("now") LocalDateTime now, Pageable pageable);


    @Query("SELECT td FROM TourDiscount td " +
            "JOIN td.tourSchedule ts " +
            "WHERE td.startDate <= :now AND td.endDate >= :now " +
            "AND td.deleted = false " +
            "AND ts.deleted = false " +
            "AND ts.published = true " +
            "AND ts.departureDate >= :now " +
            "AND ts.tour.tourStatus = com.fpt.capstone.tourism.model.enums.TourStatus.PUBLISHED " +
            "AND ts.tour.deleted = false")
    Page<TourDiscount> findActiveDiscountedTours(@Param("now") LocalDateTime now, Pageable pageable);

    Optional<TourDiscount> findFirstByTourSchedule_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
            Long scheduleId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}