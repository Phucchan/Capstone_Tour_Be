package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT COALESCE(SUM(b.adults + b.children), 0) FROM Booking b " +
            "WHERE b.tourSchedule.id = :scheduleId " +
            "AND b.bookingStatus <> com.fpt.capstone.tourism.model.enums.BookingStatus.CANCELLED")
    Integer sumGuestsByTourScheduleId(@Param("scheduleId") Long scheduleId);


    @Query(value = "SELECT t.tour_id AS tourId, t.name AS name, t.tour_type AS type, " +
            "SUM(b.total_amount) AS revenue " +
            "FROM bookings b " +
            "JOIN tour_schedules ts ON b.tour_schedule_id = ts.schedule_id " +
            "JOIN tours t ON ts.tour_id = t.tour_id " +
            "WHERE b.booking_status <> 'CANCELLED' " +
            "GROUP BY t.tour_id, t.name, t.tour_type " +
            "ORDER BY revenue DESC",
            nativeQuery = true)
    List<Object[]> findTopToursByRevenue(Pageable pageable);

    @Query(value = "SELECT EXTRACT(YEAR FROM b.created_at) AS yr, " +
            "EXTRACT(MONTH FROM b.created_at) AS mon, " +
            "SUM(b.total_amount) AS revenue " +
            "FROM bookings b " +
            "JOIN tour_schedules ts ON b.tour_schedule_id = ts.schedule_id " +
            "WHERE ts.tour_id = :tourId " +
            "AND b.booking_status <> 'CANCELLED' " +
            "AND EXTRACT(YEAR FROM b.created_at) = :year " +
            "GROUP BY yr, mon " +
            "ORDER BY mon",
            nativeQuery = true)
    List<Object[]> findMonthlyRevenueByTour(@Param("tourId") Long tourId, @Param("year") int year);


    List<Booking> findByUser_UsernameOrderByCreatedAtDesc(String username);

    Page<Booking> findByUser_Id(Long userId, Pageable pageable);

    Page<Booking> findByUser_Username(String username, Pageable pageable);


    Page<Booking> findBySellerIsNullOrderByCreatedAtAsc(Pageable pageable);


    Page<Booking> findBySeller_UsernameOrderByUpdatedAtDesc(String username, Pageable pageable);

    long countByUser_Id(Long userId);
}
