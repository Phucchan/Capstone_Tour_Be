package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourMapper {

    @Mapping(source = "departLocation.name", target = "locationName")
    @Mapping(source = "region", target = "region")
    TourSummaryDTO tourToTourSummaryDTO(Tour tour);
}
