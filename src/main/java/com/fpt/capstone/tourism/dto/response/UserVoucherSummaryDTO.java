package com.fpt.capstone.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVoucherSummaryDTO {
    private Long id; // user voucher id
    private Long voucherId;
    private String code;
    private double discountAmount;
}
