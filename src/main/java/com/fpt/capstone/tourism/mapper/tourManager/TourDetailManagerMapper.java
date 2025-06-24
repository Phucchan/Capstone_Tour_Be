package com.fpt.capstone.tourism.mapper.tourManager;

import com.fpt.capstone.tourism.dto.response.tourManager.TourDetailManagerDTO;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourDetailManagerMapper {
    @Mapping(source = "tourTheme.name", target = "tourThemeName")
    @Mapping(source = "departLocation.name", target = "departLocationName")
    @Mapping(source = "destinationLocation.name", target = "destinationLocationName")
    TourDetailManagerDTO toDTO(Tour tour);
}