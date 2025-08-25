package com.fpt.capstone.tourism.mapper.tourManager;

import com.fpt.capstone.tourism.dto.response.tourManager.TourResponseManagerDTO;
import com.fpt.capstone.tourism.model.tour.Tour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourManagementMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "thumbnailImage", source = "thumbnailUrl")
    @Mapping(target = "typeName", source = "tourType", qualifiedByName = "mapTourType")
    @Mapping(target = "tourStatus", source = "tourStatus", qualifiedByName = "mapTourStatus")
    @Mapping(target = "durationDays", source = "durationDays")
    @Mapping(target = "createdAt", source = "createdAt")
   // @Mapping(target = "createdByName", source = "createdBy.fullName")
    TourResponseManagerDTO toTourResponseDTO(Tour tour);

    @Named("mapTourType")
    default String mapTourType(Enum<?> tourType) {
        return tourType != null ? tourType.name() : null;
    }

    // Convert enum TourStatus to String
    @Named("mapTourStatus")
    default String mapTourStatus(Enum<?> tourStatus) {
        return tourStatus != null ? tourStatus.name() : null;
    }
}
