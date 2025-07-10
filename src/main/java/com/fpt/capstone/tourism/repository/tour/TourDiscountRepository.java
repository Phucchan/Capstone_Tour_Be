package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourDiscount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TourDiscountRepository extends JpaRepository<TourDiscount, Long> {


@Query("SELECT td FROM TourDiscount td " +
        "WHERE td.startDate <= :now AND td.endDate >= :now " +
        "AND td.tour.tourStatus = com.fpt.capstone.tourism.model.enums.TourStatus.PUBLISHED " +
        "AND td.tour.deleted = false " +
        "ORDER BY td.discountPercent DESC")
List<TourDiscount> findTopDiscountedTours(@Param("now") LocalDateTime now, Pageable pageable);
}