package com.fpt.capstone.tourism.dto.response.tour;

import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
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
    private List<PublicLocationDTO> departures;
    private List<PublicLocationDTO> destinations;
}