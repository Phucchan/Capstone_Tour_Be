package com.fpt.capstone.tourism.dto.response.tourManager;

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
public class TourDetailManagerDTO {
    private Long id;
    private String code;
    private String name;
    private String thumbnailUrl;
    private String description;
    private String tourType;
    private String tourStatus;
    private Integer durationDays;

    // CÁC TRƯỜNG ĐÃ ĐƯỢC SỬA LẠI ĐỂ CHỨA OBJECT {ID, NAME}
    private LocationShortDTO departLocation;
    private List<LocationShortDTO> destinations;
    private List<TourThemeOptionDTO> themes;
}
