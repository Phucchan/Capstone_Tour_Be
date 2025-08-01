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

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestBookingMapper extends EntityMapper<RequestBookingDTO, RequestBooking> {

    @Override
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "departLocation", source = "departLocationId", qualifiedByName = "mapLocation")
    RequestBooking toEntity(RequestBookingDTO dto);

    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "departLocationId", source = "departLocation.id")
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
}