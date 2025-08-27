package com.fpt.capstone.tourism.dto.response.accountant;

import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import com.fpt.capstone.tourism.model.payment.PaymentType;
import com.fpt.capstone.tourism.model.payment.PaymentBillItemStatus;
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
public class PaymentBillListDTO {
    private Long billId;
    private String billNumber;
    private String bookingCode;
    private String payTo;
    private String paidBy;
    private LocalDateTime createdDate;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;
    private PaymentBillItemStatus status;
}
