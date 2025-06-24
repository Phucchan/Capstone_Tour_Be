package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TourScheduleRepository extends JpaRepository<TourSchedule, Long> {
    /**
     * Finds all future and current schedules for a given tour.
     * @param tourId The ID of the tour.
     * @param now The current date and time.
     * @return A list of upcoming TourSchedule entities.
     */
    List<TourSchedule> findByTourIdAndDepartureDateAfter(Long tourId, LocalDateTime now);
}
