package com.fpt.capstone.tourism.helper;

import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final BookingRepository bookingRepository;

    // Chạy mỗi 1 phút
    @Scheduled(fixedRate = 60 * 1000)
    void cancelUnpaidBookings() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        List<Booking> bookings = bookingRepository
                .findByBookingStatusAndExpiredAtBefore(BookingStatus.PENDING, now);

        if (!bookings.isEmpty()) {
            bookings.forEach(b -> b.setBookingStatus(BookingStatus.CANCELLED));
            bookingRepository.saveAll(bookings);
        }
    }
}
