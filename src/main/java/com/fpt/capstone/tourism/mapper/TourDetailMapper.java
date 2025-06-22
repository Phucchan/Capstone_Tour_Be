package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.tourManager.TourDetailDTO;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourDetailMapper {
    @Mapping(source = "tourTheme.name", target = "tourThemeName")
    @Mapping(source = "departLocation.name", target = "departLocationName")
    @Mapping(source = "destinationLocation.name", target = "destinationLocationName")
    TourDetailDTO toDTO(Tour tour);
}