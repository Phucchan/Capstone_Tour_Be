package com.fpt.capstone.tourism.dto.response.tourManager;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourThemeOptionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourOptionsDTO {
    private List<TourThemeOptionDTO> themes;
    private List<LocationShortDTO> destinations;
    private List<LocationShortDTO> departures;
}