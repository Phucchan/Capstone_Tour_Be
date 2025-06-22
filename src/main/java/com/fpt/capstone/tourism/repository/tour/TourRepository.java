package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>{
    /**
     * Finds a paginated list of tours by their type and status.
     * Spring Data JPA will automatically generate the query based on the method name.
     * @param tourType The type of the tour (e.g., FIXED).
     * @param tourStatus The status of the tour (e.g., PUBLISHED).
     * @param pageable The pagination information (page number, size).
     * @return A page of tours.
     */
    Page<Tour> findByTourTypeAndTourStatus(TourType tourType, TourStatus tourStatus, Pageable pageable);
}
