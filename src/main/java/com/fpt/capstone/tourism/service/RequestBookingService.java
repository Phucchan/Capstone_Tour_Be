package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.dto.response.RequestBookingNotificationDTO;
import com.fpt.capstone.tourism.model.RequestBooking;

import java.util.List;

public interface RequestBookingService {
    GeneralResponse<RequestBookingDTO> createRequest(RequestBookingDTO requestBookingDTO);

    GeneralResponse<RequestBookingDTO> getRequest(Long id);

    GeneralResponse<PagingDTO<RequestBookingNotificationDTO>> getRequests(int page, int size);

}