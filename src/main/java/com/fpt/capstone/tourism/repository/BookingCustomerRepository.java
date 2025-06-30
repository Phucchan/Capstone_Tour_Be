package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.tour.BookingCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingCustomerRepository extends JpaRepository<BookingCustomer, Long> {
    List<BookingCustomer> findByBooking_Id(Long bookingId);
}