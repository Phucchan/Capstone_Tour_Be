package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourManagementRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {

    boolean existsByCode(String code);

    @Query("SELECT t FROM Tour t WHERE t.deleted = false " +
            "AND (:keyword IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR t.tourStatus = :status) " +
            "AND ((:hasDiscount IS NULL) OR " +
            "(:hasDiscount = TRUE AND EXISTS (SELECT 1 FROM TourDiscount td JOIN td.tourSchedule ts " +
            "WHERE td.deleted = false AND ts.tour = t)) OR " +
            "(:hasDiscount = FALSE AND NOT EXISTS (SELECT 1 FROM TourDiscount td JOIN td.tourSchedule ts " +
            "WHERE td.deleted = false AND ts.tour = t)))")
    Page<Tour> findToursForDiscount(@Param("keyword") String keyword,
                                    @Param("status") TourStatus status,
                                    @Param("hasDiscount") Boolean hasDiscount,
                                    Pageable pageable);
    }
