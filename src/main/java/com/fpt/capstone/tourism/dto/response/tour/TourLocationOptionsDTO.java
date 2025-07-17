package com.fpt.capstone.tourism.dto.response.tour;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourLocationOptionsDTO {
    private List<LocationDTO> departures;
    private List<LocationDTO> destinations;
}