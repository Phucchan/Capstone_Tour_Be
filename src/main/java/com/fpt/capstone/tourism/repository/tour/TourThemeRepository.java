package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourTheme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourThemeRepository extends JpaRepository<TourTheme, Long> {
    @Query("SELECT tt FROM TourTheme tt WHERE tt.tour.id = ?1 AND (tt.deleted = false OR tt.deleted IS NULL)")
    List<TourTheme> findByTourId(Long tourId);

}
