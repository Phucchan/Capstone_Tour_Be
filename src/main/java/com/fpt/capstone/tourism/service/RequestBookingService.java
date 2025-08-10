package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.RequestBookingNotificationDTO;
import com.fpt.capstone.tourism.model.RequestBooking;

import java.util.List;

public interface RequestBookingService {
    GeneralResponse<RequestBookingDTO> createRequest(RequestBookingDTO requestBookingDTO);

    GeneralResponse<RequestBookingDTO> getRequest(Long id);

    GeneralResponse<PagingDTO<RequestBookingNotificationDTO>> getRequests(int page, int size);

    GeneralResponse<RequestBookingDTO> updateStatus(Long id, ChangeStatusDTO changeStatusDTO);

    GeneralResponse<PagingDTO<RequestBookingSummaryDTO>> getRequestsByUser(Long userId, int page, int size, String search);

    GeneralResponse<RequestBookingDTO> rejectRequest(Long id, String reason);
}