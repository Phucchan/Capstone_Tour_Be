package com.fpt.capstone.tourism.dto.response.tourManager;

import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDayManagerDTO {
    private Long id;
    private int dayNumber;
    private String title;
    private String description;
    private LocationShortDTO location;
    private List<ServiceTypeShortDTO> serviceTypes;
    private List<ServiceInfoDTO> services;
}