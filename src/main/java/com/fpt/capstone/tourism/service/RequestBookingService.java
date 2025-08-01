package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.model.RequestBooking;

public interface RequestBookingService {
    GeneralResponse<RequestBookingDTO> createRequest(RequestBookingDTO requestBookingDTO);

    GeneralResponse<RequestBookingDTO> getRequest(Long id);
}