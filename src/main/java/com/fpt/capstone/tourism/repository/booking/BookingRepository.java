package com.fpt.capstone.tourism.repository.booking;

import com.fpt.capstone.tourism.model.tour.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT COALESCE(SUM(b.adults + b.children + b.infants + b.toddlers), 0) FROM Booking b " +
            "WHERE b.tourSchedule.id = :scheduleId " +
            "AND b.bookingStatus <> com.fpt.capstone.tourism.model.enums.BookingStatus.CANCELLED")
    Integer sumGuestsByTourScheduleId(@Param("scheduleId") Long scheduleId);


    // SỬA LỖI: Thêm CAST(... AS timestamp)
    @Query(value = "SELECT t.tour_id AS tourId, t.name AS name, t.tour_type AS type, " +
            "SUM(b.total_amount) AS revenue " +
            "FROM bookings b " +
            "JOIN tour_schedules ts ON b.tour_schedule_id = ts.schedule_id " +
            "JOIN tours t ON ts.tour_id = t.tour_id " +
            "WHERE b.booking_status <> 'CANCELLED' " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR b.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR b.created_at <= :endDate) " +
            "GROUP BY t.tour_id, t.name, t.tour_type " +
            "ORDER BY revenue DESC",
            nativeQuery = true)
    List<Object[]> findTopToursByRevenue(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);

    // SỬA LỖI: Thêm CAST(... AS timestamp)
    @Query(value = "SELECT EXTRACT(YEAR FROM b.created_at) AS yr, " +
            "EXTRACT(MONTH FROM b.created_at) AS mon, " +
            "SUM(b.total_amount) AS revenue " +
            "FROM bookings b " +
            "JOIN tour_schedules ts ON b.tour_schedule_id = ts.schedule_id " +
            "WHERE ts.tour_id = :tourId " +
            "AND b.booking_status <> 'CANCELLED' " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR b.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR b.created_at <= :endDate) " +
            "GROUP BY yr, mon " +
            "ORDER BY mon",
            nativeQuery = true)
    List<Object[]> findMonthlyRevenueByTour(@Param("tourId") Long tourId,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT EXTRACT(YEAR FROM b.created_at) AS yr, " +
            "EXTRACT(MONTH FROM b.created_at) AS mon, " +
            "SUM(b.total_amount) AS revenue " +
            "FROM bookings b " +
            "WHERE b.booking_status <> 'CANCELLED' " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR b.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR b.created_at <= :endDate) " +
            "GROUP BY yr, mon " +
            "ORDER BY mon",
            nativeQuery = true)
    List<Object[]> findMonthlyRevenueSummary(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);


    Page<Booking> findByUser_Id(Long userId, Pageable pageable);

    Page<Booking> findByTourSchedule_Tour_NameContainingIgnoreCase(String name, Pageable pageable);

    Page<Booking> findBySellerIsNull(Pageable pageable);


    Page<Booking> findBySeller_UsernameOrderByUpdatedAtDesc(String username, Pageable pageable);

    long countByUser_Id(Long userId);

    Booking findByBookingCode(String bookingCode);
    // SỬA LỖI: Thêm CAST(... AS timestamp)
    @Query(value = "SELECT COALESCE(SUM(b.total_amount), 0) FROM bookings b " +
            "WHERE b.booking_status <> 'CANCELLED' " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR b.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR b.created_at <= :endDate)",
            nativeQuery = true)
    Double calculateTotalRevenue(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    // SỬA LỖI: Thêm CAST(... AS timestamp)
    @Query(value = "SELECT COUNT(*) FROM bookings b " +
            "WHERE b.booking_status <> 'CANCELLED' " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR b.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR b.created_at <= :endDate)",
            nativeQuery = true)
    Long countBookings(@Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(*) FROM bookings b " +
            "WHERE b.booking_status = 'CANCELLED' " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR b.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR b.created_at <= :endDate)",
            nativeQuery = true)
    Long countCancelledBookings(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    // SỬA LỖI: Thêm CAST(... AS timestamp)
    @Query(value = "SELECT COUNT(*) FROM (" +
            "SELECT b.user_id FROM bookings b " +
            "WHERE b.booking_status <> 'CANCELLED' " +
            "AND (CAST(:startDate AS timestamp) IS NULL OR b.created_at >= :startDate) " +
            "AND (CAST(:endDate AS timestamp) IS NULL OR b.created_at <= :endDate) " +
            "GROUP BY b.user_id HAVING COUNT(*) > 1) t",
            nativeQuery = true)
    Long countReturningCustomers(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT b.booking_id AS booking_id, t.code AS tour_code, t.name AS tour_name, t.tour_type AS tour_type, " +
            "ts.departure_date AS start_date, b.booking_status AS status, u.full_name AS customer_name " +
            "FROM bookings b " +
            "LEFT JOIN tour_schedules ts ON b.tour_schedule_id = ts.schedule_id " +
            "LEFT JOIN tours t ON ts.tour_id = t.tour_id " +
            "LEFT JOIN users u ON b.user_id = u.id " +
            "WHERE b.booking_status = 'CANCEL_REQUESTED' " +
            "AND (:search IS NULL OR LOWER(t.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :search, '%'))) ",
            countQuery = "SELECT COUNT(*) FROM bookings b " +
                    "LEFT JOIN tour_schedules ts ON b.tour_schedule_id = ts.schedule_id " +
                    "LEFT JOIN tours t ON ts.tour_id = t.tour_id " +
                    "LEFT JOIN users u ON b.user_id = u.id " +
                    "WHERE b.booking_status = 'CANCEL_REQUESTED' " +
                    "AND (:search IS NULL OR LOWER(t.code) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(u.full_name) LIKE LOWER(CONCAT('%', :search, '%'))) ",
            nativeQuery = true)
    Page<Object[]> findRefundRequests(@Param("search") String search, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(@Param("id") Long id);

    List<Booking> findByUser_IdAndBookingStatus(Long userId, com.fpt.capstone.tourism.model.enums.BookingStatus status);
}
