package com.fpt.capstone.tourism.dto.response.accountant;

import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import com.fpt.capstone.tourism.model.payment.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundBillDTO {
    private String bookingCode;
    private String payTo;
    private String paidBy;
    private LocalDateTime createdDate;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private String note;
    private BigDecimal totalAmount;
    private List<RefundBillItemDTO> items;
}