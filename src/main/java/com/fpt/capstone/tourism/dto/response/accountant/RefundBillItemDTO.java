package com.fpt.capstone.tourism.dto.response.accountant;

import com.fpt.capstone.tourism.model.payment.PaymentBillItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundBillItemDTO {
    private String content;
    private int unitPrice;
    private Integer quantity;
    private double discount;
    private BigDecimal amount;
    private PaymentBillItemStatus status;
}
