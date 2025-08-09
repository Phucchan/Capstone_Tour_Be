package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.payment.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByBooking_Id(Long bookingId);
}