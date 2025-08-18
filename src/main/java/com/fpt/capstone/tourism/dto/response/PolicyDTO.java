package com.fpt.capstone.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDTO {
    private String tourPolicy;
    private String cancellationPolicy;
    private String bookingPolicy;
    private String tourPrice;
}