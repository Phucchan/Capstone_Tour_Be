package com.fpt.capstone.tourism.dto.common.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerShortDTO {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private String contactEmail;
    private String contactPhone;
    private String partnerType;
    private long locationId;
}
