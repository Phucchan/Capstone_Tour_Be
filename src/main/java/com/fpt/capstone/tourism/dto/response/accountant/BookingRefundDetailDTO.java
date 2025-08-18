package com.fpt.capstone.tourism.dto.response.accountant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRefundDetailDTO {
    private Long bookingId;
    private String bookingCode;
    private String tourCode;
    private String tourName;
    private String tourType;
    private LocalDateTime startDate;
    private String status;
    private String customerName;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private BigDecimal refundAmount;
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String bankName;
    private RefundBillDTO refundBill;
}