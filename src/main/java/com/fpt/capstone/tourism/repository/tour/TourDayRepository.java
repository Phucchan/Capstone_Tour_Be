package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourDay;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TourDayRepository extends JpaRepository<TourDay, Long> {
    @EntityGraph(attributePaths = {"services"})
    @Query("SELECT td FROM TourDay td WHERE td.tour.id = ?1 ORDER BY td.dayNumber ASC")
    List<TourDay> findByTourIdOrderByDayNumberAsc(Long tourId);
    /**
     * Lấy danh sách TourDay của một tour, đồng thời tải lên (fetch) danh sách các dịch vụ
     * liên quan một cách tường minh bằng JOIN FETCH để tránh lỗi LazyInitializationException.
     * @param tourId ID của tour cần lấy dữ liệu.
     * @return Danh sách các TourDay với đầy đủ thông tin dịch vụ.
     */
    @Query("SELECT DISTINCT td FROM TourDay td LEFT JOIN FETCH td.services WHERE td.tour.id = :tourId ORDER BY td.dayNumber ASC")
    List<TourDay> findByTourIdWithServices(@Param("tourId") Long tourId);

    @Query("SELECT COUNT(td) FROM TourDay td WHERE td.tour.id = :tourId")
    long countByTourId(@Param("tourId") Long tourId);
}