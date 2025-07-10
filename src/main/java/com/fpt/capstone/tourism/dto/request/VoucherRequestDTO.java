package com.fpt.capstone.tourism.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherRequestDTO {
    private String code;
    private String description;
    private double discountAmount;
    private Integer pointsRequired;
    private double minOrderValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private Integer maxUsage;
    private Long createdBy;
}