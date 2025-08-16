package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fpt.capstone.tourism.model.Location;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour>{
    /**
     * Finds a paginated list of tours by their type and status.
     * Spring Data JPA will automatically generate the query based on the method name.
     * @param tourType The type of the tour (e.g., FIXED).
     * @param tourStatus The status of the tour (e.g., PUBLISHED).
     * @param pageable The pagination information (page number, size).
     * @return A page of tours.
     */
    Page<Tour> findByTourTypeAndTourStatus(TourType tourType, TourStatus tourStatus, Pageable pageable);
    Page<Tour> findByDepartLocationIdAndTourStatus(Long locationId, TourStatus tourStatus, Pageable pageable);
    /**
     * Lấy danh sách các ĐIỂM KHỞI HÀNH duy nhất từ các tour đã xuất bản.
     */
    @Query("SELECT DISTINCT t.departLocation FROM Tour t WHERE t.tourStatus = 'PUBLISHED' AND t.deleted = false")
    List<Location> findDistinctDepartLocations();

    /**
     * Lấy danh sách các ĐIỂM ĐẾN duy nhất từ các tour đã xuất bản.
     */
    @Query("SELECT DISTINCT td.location FROM Tour t JOIN t.tourDays td WHERE t.tourStatus = 'PUBLISHED' AND t.deleted = false")
    List<Location> findDistinctDestinations();

    @Query("SELECT t FROM Tour t WHERE t.requestBooking.user.id = :userId AND t.tourType = :tourType " +
            "AND EXISTS (SELECT ts FROM TourSchedule ts WHERE ts.tour = t AND ts.departureDate > CURRENT_TIMESTAMP)")
    Page<Tour> findCustomToursByUserWithSchedules(@Param("userId") Long userId,
                                                  @Param("tourType") TourType tourType,
                                                  Pageable pageable);

    @Query("SELECT t FROM Tour t WHERE t.tourType = :tourType AND t.requestBooking.user.id = :userId " +
            "AND LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND EXISTS (SELECT ts FROM TourSchedule ts WHERE ts.tour = t AND ts.departureDate > CURRENT_TIMESTAMP)")
    Page<Tour> searchCustomToursByUser(@Param("userId") Long userId,
                                       @Param("tourType") TourType tourType,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);

}
