package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourDayRepository extends JpaRepository<TourDay, Long> {
    List<TourDay> findByTourIdOrderByDayNumberAsc(Long tourId);
}