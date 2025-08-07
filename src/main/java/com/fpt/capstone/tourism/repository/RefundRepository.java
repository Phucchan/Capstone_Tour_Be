package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.payment.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
}