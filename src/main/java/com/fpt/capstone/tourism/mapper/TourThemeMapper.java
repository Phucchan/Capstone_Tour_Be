package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.homepage.TourThemeDTO;
import com.fpt.capstone.tourism.model.tour.TourTheme;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourThemeMapper {
    TourThemeDTO tourThemeToTourThemeDTO(TourTheme tourTheme);
}
