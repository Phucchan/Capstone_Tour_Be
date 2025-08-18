package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;

public interface TourPaxManagerService {
    GeneralResponse<TourPaxManagerDTO> createTourPax(Long tourId, TourPaxManagerCreateRequestDTO requestDTO);

    GeneralResponse<TourPaxManagerDTO> getTourPax(Long tourId, Long paxId);

    GeneralResponse<TourPaxManagerDTO> updateTourPax(Long tourId, Long paxId, TourPaxManagerCreateRequestDTO requestDTO);

    GeneralResponse<String> deleteTourPax(Long tourId, Long paxId);
}
