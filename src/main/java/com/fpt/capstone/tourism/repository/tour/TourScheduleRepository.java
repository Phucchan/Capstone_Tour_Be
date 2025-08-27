package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourSchedule;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TourScheduleRepository extends JpaRepository<TourSchedule, Long> {

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

    List<TourSchedule> findByTourIdAndDepartureDateAfterAndDeletedFalseOrderByDepartureDateAsc(Long tourId, LocalDateTime now);

    List<TourSchedule> findByTourId(Long tourId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ts from TourSchedule ts where ts.id = :id")
    Optional<TourSchedule> findByIdForUpdate(@Param("id") Long id);
}
