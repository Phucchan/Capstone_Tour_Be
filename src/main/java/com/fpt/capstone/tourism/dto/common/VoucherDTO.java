package com.fpt.capstone.tourism.dto.common;

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
public class VoucherDTO {
    private Long id;
    private String code;
    private String description;
    private double discountAmount;
    private Integer pointsRequired;
    private double minOrderValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private VoucherStatus voucherStatus;
    private Integer maxUsage;
    private String createdByName;
    private Boolean deleted;
}