package com.fpt.capstone.tourism.repository.booking;

import com.fpt.capstone.tourism.model.tour.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingServiceRepository extends JpaRepository<BookingService, Long> {
    @Query("SELECT bs FROM BookingService bs JOIN FETCH bs.service WHERE bs.booking.id = :bookingId")
    List<BookingService> findWithServiceByBookingId(@Param("bookingId") Long bookingId);
}
