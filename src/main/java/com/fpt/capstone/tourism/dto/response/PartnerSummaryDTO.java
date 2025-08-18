package com.fpt.capstone.tourism.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerSummaryDTO {
    private Long id;
    private String name;
    private String contactPhone;
    private String contactEmail;
    private Boolean deleted;
}