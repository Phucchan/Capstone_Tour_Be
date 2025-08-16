package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourMapper {

    @Mapping(source = "departLocation.name", target = "locationName")
    @Mapping(source = "region", target = "region")
    @Mapping(source = "tourTransport", target = "tourTransport", qualifiedByName = "transportToString")
    @Mapping(target = "scheduleId", ignore = true)
    @Mapping(target = "departureDates", ignore = true)
    TourSummaryDTO tourToTourSummaryDTO(Tour tour);
    @Named("transportToString")
    default String transportToString(TourTransport transport) {
        return transport != null ? transport.name() : null;
    }
}
