package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPriceCalculateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.ServiceBreakdownDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxFullDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxUpdateRequestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TourPaxService {
    GeneralResponse<List<TourPaxFullDTO>> getTourPaxConfigurations(Long tourId);

    GeneralResponse<TourPaxFullDTO> getTourPaxConfiguration(Long tourId, Long paxId);

    GeneralResponse<TourPaxFullDTO> createTourPaxConfiguration(Long tourId, TourPaxCreateRequestDTO request);

    GeneralResponse<TourPaxFullDTO> updateTourPaxConfiguration(Long tourId, Long paxId, TourPaxUpdateRequestDTO request);

    GeneralResponse<String> deleteTourPaxConfiguration(Long tourId, Long paxId);

    @Transactional
    GeneralResponse<List<TourPaxFullDTO>> calculatePrices(Long tourId, TourPriceCalculateRequestDTO request);

    GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId);

}
