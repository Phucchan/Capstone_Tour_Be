package com.fpt.capstone.tourism.dto.response.accountant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingSettlementDTO {
    private Long bookingId;
    private String bookingCode;
    private String tourName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String tourType;
    private Integer duration;
    private String status;
    private List<BookingServiceSettlementDTO> services;
    private List<PaymentBillListDTO> receiptBills;
    private List<PaymentBillListDTO> paymentBills;
    private List<PaymentBillListDTO> refundBills;
}

