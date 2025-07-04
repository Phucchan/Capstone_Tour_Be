package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.model.RequestBooking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestBookingMapper extends EntityMapper<RequestBookingDTO, RequestBooking> {

    @Override
    RequestBooking toEntity(RequestBookingDTO dto);

    @Override
    RequestBookingDTO toDTO(RequestBooking entity);
}