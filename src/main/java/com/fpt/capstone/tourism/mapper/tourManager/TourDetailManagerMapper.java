package com.fpt.capstone.tourism.mapper.tourManager;

import com.fpt.capstone.tourism.dto.response.tourManager.TourDetailManagerDTO;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;


@Component("TourDetailManagerMapper")
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourDetailManagerMapper {
    @Mapping(source = "tourTheme.name", target = "tourThemeName")
    @Mapping(source = "departLocation.name", target = "departLocationName")
    @Mapping(source = "code", target = "code")
    TourDetailManagerDTO toDTO(Tour tour);
}