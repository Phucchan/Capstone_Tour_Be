package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourScheduleRepository extends JpaRepository<TourSchedule, Long> {
    /**
     * Finds all future and current schedules for a given tour.
     * @param tourId The ID of the tour.
     * @param now The current date and time.
     * @return A list of upcoming TourSchedule entities.
     */
    List<TourSchedule> findByTourIdAndDepartureDateAfter(Long tourId, LocalDateTime now);

//    /**
//     * Finds the single next available schedule for a tour.
//     * It looks for schedules with a departure date after the current time and
//     * orders them by date to get the soonest one.
//     * @param tourId The ID of the tour.
//     * @param now The current date and time.
//     * @return An Optional containing the next TourSchedule, or empty if none found.
//     */
//    Optional<TourSchedule> findFirstByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(Long tourId, LocalDateTime now);

    /**
     * CẬP NHẬT/THAY THẾ:
     * Finds ALL upcoming schedules for a given tour, ordered by date.
     * @param tourId The ID of the tour.
     * @param now The current date and time.
     * @return A List of all upcoming TourSchedule entities.
     */
    List<TourSchedule> findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(Long tourId, LocalDateTime now);

    List<TourSchedule> findByTourId(Long tourId);
}
