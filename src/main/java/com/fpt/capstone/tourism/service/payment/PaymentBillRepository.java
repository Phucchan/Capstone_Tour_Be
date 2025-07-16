package com.fpt.capstone.tourism.service.payment;

import com.fpt.capstone.tourism.model.payment.PaymentBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentBillRepository extends JpaRepository<PaymentBill, Long> {
    List<PaymentBill> findPaymentBillsByBookingCode(String bookingCode);
}
