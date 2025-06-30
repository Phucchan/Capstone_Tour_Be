package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourDay;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourDayRepository extends JpaRepository<TourDay, Long> {
    @EntityGraph(attributePaths = {"services"})
    @Query("SELECT td FROM TourDay td WHERE td.tour.id = ?1 ORDER BY td.dayNumber ASC")
    List<TourDay> findByTourIdOrderByDayNumberAsc(Long tourId);


}