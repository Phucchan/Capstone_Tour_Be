package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.model.enums.VoucherStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherSummaryDTO {
    private Long id;
    private String code;
    private double discountAmount;
    private Integer pointsRequired;
    private double minOrderValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private VoucherStatus voucherStatus;
    private Integer maxUsage;
}