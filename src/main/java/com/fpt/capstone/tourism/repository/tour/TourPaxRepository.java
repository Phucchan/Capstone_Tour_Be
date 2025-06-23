package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourPax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPaxRepository extends JpaRepository<TourPax, Long> {
    List<TourPax> findByTourId(Long tourId);
}