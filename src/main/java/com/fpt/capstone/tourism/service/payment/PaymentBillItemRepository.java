package com.fpt.capstone.tourism.service.payment;

import com.fpt.capstone.tourism.model.payment.PaymentBillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentBillItemRepository extends JpaRepository<PaymentBillItem, Long> {
    @Query("SELECT pbi FROM PaymentBillItem pbi WHERE pbi.paymentBill.bookingCode = ?1")
    List<PaymentBillItem> findAllByBookingCode(String bookingCode);
}
