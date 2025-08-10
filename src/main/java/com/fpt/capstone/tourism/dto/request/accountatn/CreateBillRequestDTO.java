package com.fpt.capstone.tourism.dto.request.accountatn;

import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import com.fpt.capstone.tourism.model.payment.PaymentBillItemStatus;
import com.fpt.capstone.tourism.model.payment.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillRequestDTO {
    // Fields for PaymentBill
    private String payTo;
    private String paidBy;
    private LocalDateTime createdDate;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private String note;

    // Fields for PaymentBillItem
    private String content;
    private int unitPrice;
    private Integer quantity;
    private double discount;
    private BigDecimal amount;
}
