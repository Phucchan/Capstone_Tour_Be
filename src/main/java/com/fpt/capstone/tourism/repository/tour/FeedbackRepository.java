package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.Feedback;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    /**
     * Finds tours with the highest average ratings.
     * This JPQL query joins Feedback with Tour, groups by Tour, calculates the average rating,
     * and orders the results in descending order of the average rating.
     * It returns a list of Tour entities.
     */
    @Query("SELECT f.booking.tourSchedule.tour FROM Feedback f " +
            "WHERE f.booking.tourSchedule.tour.tourStatus = com.fpt.capstone.tourism.model.enums.TourStatus.PUBLISHED " +
            "AND f.booking.tourSchedule.tour.deleted = false " +
            "GROUP BY f.booking.tourSchedule.tour " +
            "ORDER BY AVG(f.rating) DESC, COUNT(f) DESC")
    List<Tour> findTopRatedTours(Pageable pageable);

    /**
     * Calculates the average rating for a single tour.
     */
    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.booking.tourSchedule.tour.id = :tourId")
    Double findAverageRatingByTourId(Long tourId);
}
