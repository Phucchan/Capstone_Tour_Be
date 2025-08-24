package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourDiscount;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
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

    @Query(value = "SELECT td.* FROM tour_discounts td " +
            "JOIN tour_schedules ts ON td.schedule_id = ts.schedule_id " +
            "JOIN tours t ON ts.tour_id = t.tour_id " +
            "WHERE td.is_deleted = false " +
            "AND ts.is_deleted = false " +
            "AND t.is_deleted = false " +
            "AND (CAST(:keyword AS text) IS NULL OR LOWER(CAST(t.name AS TEXT)) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            nativeQuery = true)
    Page<TourDiscount> searchActiveDiscounts(@Param("keyword") String keyword, Pageable pageable);

    Optional<TourDiscount> findFirstByTourSchedule_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
            Long scheduleId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    Optional<TourDiscount> findByTourSchedule_IdAndDeletedFalse(Long scheduleId);


    Optional<TourDiscount> findByIdAndDeletedFalse(Long id);

    @Query("SELECT td FROM TourDiscount td " +
            "WHERE td.tourSchedule.id = :scheduleId " +
            "AND td.deleted = false " +
            "AND td.startDate < :endDate " +
            "AND td.endDate > :startDate " +
            "AND (:currentDiscountId IS NULL OR td.id <> :currentDiscountId)")
    Optional<TourDiscount> findOverlappingDiscounts(@Param("scheduleId") Long scheduleId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("currentDiscountId") Long currentDiscountId);
}
