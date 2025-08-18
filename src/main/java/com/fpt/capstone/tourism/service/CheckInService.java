package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.CheckInDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CheckInService {
    GeneralResponse<List<BookingSummaryDTO>> getCompletedTours(Long userId);

    GeneralResponse<List<CheckInDTO>> getCheckIns(Long userId, Long bookingId);

    GeneralResponse<CheckInDTO> addCheckIn(Long userId, Long bookingId, MultipartFile file);

    GeneralResponse<String> deleteCheckIn(Long userId, Long checkInId);

}