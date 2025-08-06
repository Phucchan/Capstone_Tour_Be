package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    List<CheckIn> findByBooking_Id(Long bookingId);

    Optional<CheckIn> findByIdAndBooking_User_Id(Long id, Long userId);
}
