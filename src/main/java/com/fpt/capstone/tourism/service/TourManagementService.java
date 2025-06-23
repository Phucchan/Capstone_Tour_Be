package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TourManagementService {

    public GeneralResponse<List<TourResponseDTO>> getListTours();

    public GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO);

    GeneralResponse<Object> getTourDetail(Long id);

    GeneralResponse<TourDetailDTO> updateTour(Long id, TourUpdateRequestDTO requestDTO);

    GeneralResponse<List<TourDayDTO>> getTourDays(Long tourId);

    GeneralResponse<TourDayDTO> createTourDay(Long tourId, TourDayCreateRequestDTO requestDTO);

    GeneralResponse<TourDayDTO> updateTourDay(Long tourId, Long dayId, TourDayCreateRequestDTO requestDTO);

    GeneralResponse<String> deleteTourDay(Long tourId, Long dayId);

    GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId);

    GeneralResponse<TourPaxDTO> createTourPax(Long tourId, TourPaxCreateRequestDTO requestDTO);
}
