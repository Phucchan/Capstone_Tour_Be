package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationWithoutGeoPositionDTO;
import com.fpt.capstone.tourism.dto.common.location.PublicLocationSimpleDTO;
import com.fpt.capstone.tourism.dto.request.LocationRequestDTO;
import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import com.fpt.capstone.tourism.dto.response.homepage.PopularLocationDTO;
import com.fpt.capstone.tourism.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper extends EntityMapper<LocationDTO, Location>{
    Location toEntity(LocationRequestDTO requestDTO);
    PublicLocationDTO toPublicLocationDTO(Location location);
    PopularLocationDTO toPopularLocationDTO(Location location);
    PublicLocationSimpleDTO toPublicLocationSimpleDTO(Location location);
    Location toEntity(PublicLocationDTO publicLocationDTO);
    Location toEntity(LocationDTO locationDTO);
    LocationDTO toDTO(Location location);
    LocationShortDTO toLocationShortDTO(Location location);

    LocationWithoutGeoPositionDTO toLocationWithoutGeoPositionDTO(Location location);


    @Mapping(target = "id", source = "dto.id") // Map only 'id'
    @Mapping(target = "name", ignore = true)
    Location toLocation(LocationShortDTO dto);
}
