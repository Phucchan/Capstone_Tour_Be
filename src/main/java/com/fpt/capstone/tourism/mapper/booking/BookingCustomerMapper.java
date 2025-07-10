package com.fpt.capstone.tourism.mapper.booking;

import com.fpt.capstone.tourism.dto.common.user.BookedPersonDTO;
import com.fpt.capstone.tourism.dto.common.user.TourCustomerDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.model.tour.BookingCustomer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingCustomerMapper {
    List<BookingCustomer> toEntity(List<BookingRequestCustomerDTO> adults);
    TourCustomerDTO toDTO(BookingCustomer bookingCustomer);
    BookedPersonDTO toBookedPersonDTO(BookingCustomer bookingCustomer);
}
