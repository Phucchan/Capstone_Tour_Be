package com.fpt.capstone.tourism.repository.booking;

import com.fpt.capstone.tourism.model.tour.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingServiceRepository extends JpaRepository<BookingService, Long> {
    // Additional query methods can be defined here if needed
}
