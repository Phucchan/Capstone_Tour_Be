package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourManagementRepository extends JpaRepository<Tour, Long> {

    Page<Tour> findByNameContainingIgnoreCase(String name, Pageable pageable);


    @Query("SELECT t FROM Tour t WHERE " +
            "(:keyword IS NULL OR LOWER(t.name) LIKE CONCAT('%', :keyword, '%')) AND " +
            "(:tourType IS NULL OR t.tourType = :tourType) AND " +
            "(:tourStatus IS NULL OR t.tourStatus = :tourStatus)")
    Page<Tour> searchTours(@Param("keyword") String keyword,
                           @Param("tourType") TourType tourType,
                           @Param("tourStatus") TourStatus tourStatus,
                           Pageable pageable);
}
