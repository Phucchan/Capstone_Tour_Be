package com.fpt.capstone.tourism.mapper.booking;

import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.mapper.EntityMapper;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestBookingMapper extends EntityMapper<RequestBookingDTO, RequestBooking> {

    @Override
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "departureLocation", source = "departureLocationId", qualifiedByName = "mapLocation")
    @Mapping(target = "destinationLocations", source = "destinationLocationIds", qualifiedByName = "mapLocations")
    RequestBooking toEntity(RequestBookingDTO dto);

    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "departureLocationId", source = "departureLocation.id")
    @Mapping(target = "destinationLocationIds", source = "destinationLocations", qualifiedByName = "mapLocationIds")
    RequestBookingDTO toDTO(RequestBooking entity);


    @Named("mapUser")
    default User mapUser(Long id) {
        if (id == null) return null;
        return User.builder().id(id).build();
    }

    @Named("mapLocation")
    default Location mapLocation(Long id) {
        if (id == null) return null;
        return Location.builder().id(id).build();
    }

    @Named("mapLocations")
    default List<Location> mapLocations(List<Long> ids) {
        if (ids == null) return null;
        return ids.stream().map(this::mapLocation).collect(Collectors.toList());
    }

    @Named("mapLocationIds")
    default List<Long> mapLocationIds(List<Location> locations) {
        if (locations == null) return null;
        return locations.stream().map(Location::getId).collect(Collectors.toList());
    }
}