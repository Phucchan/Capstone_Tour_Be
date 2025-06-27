package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Tính tổng số khách (người lớn + trẻ em) đã đặt cho một lịch trình cụ thể,
     * chỉ tính các booking không bị hủy.
     * @param scheduleId ID của TourSchedule.
     * @return Tổng số khách đã đặt.
     */
    @Query("SELECT COALESCE(SUM(b.adults + b.children), 0) FROM Booking b " +
            "WHERE b.tourSchedule.id = :scheduleId " +
            "AND b.bookingStatus <> com.fpt.capstone.tourism.model.enums.BookingStatus.CANCELLED")
    Integer sumGuestsByTourScheduleId(@Param("scheduleId") Long scheduleId);
}
