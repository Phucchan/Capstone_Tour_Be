package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.tour.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourManagementRepository extends JpaRepository<Tour, Long> {

    Page<Tour> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
