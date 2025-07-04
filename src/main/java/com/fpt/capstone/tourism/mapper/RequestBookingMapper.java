package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.model.RequestBooking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestBookingMapper extends EntityMapper<RequestBookingDTO, RequestBooking> {

    @Override
    @org.mapstruct.Mapping(target = "location.id", source = "locationId")
    RequestBooking toEntity(RequestBookingDTO dto);

    @Override
    @org.mapstruct.Mapping(target = "locationId", source = "location.id")
    RequestBookingDTO toDTO(RequestBooking entity);
}