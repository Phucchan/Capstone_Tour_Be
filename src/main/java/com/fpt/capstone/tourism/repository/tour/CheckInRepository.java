package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findByBooking_IdAndDeletedFalse(Long bookingId);

    Optional<CheckIn> findByIdAndBooking_User_IdAndDeletedFalse(Long id, Long userId);

    long countByBooking_IdAndDeletedFalse(Long bookingId);
}
