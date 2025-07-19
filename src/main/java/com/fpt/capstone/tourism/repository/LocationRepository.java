package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
    Location findByName(String name);
    @Query(value = "SELECT * FROM location WHERE is_deleted = FALSE ORDER BY RANDOM() LIMIT :numberLocation", nativeQuery = true)
    List<Location> findRandomLocation(@Param("numberLocation") int numberLocation);
    @Query(value = """
            SELECT l.* FROM location l
            JOIN (
                SELECT t.location_id AS loc_id, COUNT(b.booking_id) AS cnt
                FROM bookings b
                JOIN tour_schedules ts ON b.tour_schedule_id = ts.schedule_id
                JOIN tours t ON ts.tour_id = t.tour_id
                WHERE b.booking_status <> 'CANCELLED'
                  AND t.is_deleted = false
                GROUP BY t.location_id
                ORDER BY cnt DESC
                LIMIT :limit
            ) AS sub ON l.id = sub.loc_id
            WHERE l.is_deleted = FALSE
            ORDER BY sub.cnt DESC
            """,
            nativeQuery = true)
    List<Location> findTopVisitedLocations(@Param("limit") int limit);
    Page<Location> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query(value = "SELECT * FROM location WHERE is_deleted = FALSE", nativeQuery = true)
    List<Location> findAllLocations();
}