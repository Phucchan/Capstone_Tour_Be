package com.fpt.capstone.tourism.helper;

import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 2 * 60 * 60 * 1000)
    void removeExpiredUnpaidBookings() {
        LocalDateTime cutoff = LocalDateTime.now().plusDays(3);
        List<Booking> bookings = bookingRepository
                .findByBookingStatusAndTourSchedule_DepartureDateBefore(BookingStatus.PENDING, cutoff);

        if (!bookings.isEmpty()) {
            bookings.forEach(b -> b.setBookingStatus(BookingStatus.CANCELLED));
            bookingRepository.saveAll(bookings);
        }
    }
}
