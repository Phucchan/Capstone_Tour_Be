package com.fpt.capstone.tourism.dto.response;

import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.common.PartnerServiceShortDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerDetailDTO {
    private Long id;
    private String name;
    private String logoUrl;
    private String contactPhone;
    private String contactEmail;
    private String contactName;
    private String description;
    private String websiteUrl;
    private LocationShortDTO location;
    private ServiceTypeShortDTO serviceType;
    private List<LocationShortDTO> locationOptions;
    private List<ServiceTypeShortDTO> serviceTypeOptions;
}