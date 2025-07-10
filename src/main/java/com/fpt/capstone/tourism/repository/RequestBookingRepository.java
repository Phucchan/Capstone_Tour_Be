package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.RequestBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestBookingRepository extends JpaRepository<RequestBooking, Long> {
}