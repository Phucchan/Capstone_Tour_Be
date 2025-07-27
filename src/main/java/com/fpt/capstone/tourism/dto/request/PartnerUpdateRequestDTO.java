package com.fpt.capstone.tourism.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerUpdateRequestDTO {
    private String name;
    private String logoUrl;
    private String contactPhone;
    private String contactEmail;
    private String contactName;
    private String description;
    private String websiteUrl;
    private Long locationId;
    private Long serviceTypeId;
}