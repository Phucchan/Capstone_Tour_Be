package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TourManagementService {

    GeneralResponse<PagingDTO<TourResponseManagerDTO>> getListTours(int page, int size);

    public GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO);

    GeneralResponse<Object> getTourDetail(Long id);

    GeneralResponse<TourDetailManagerDTO> updateTour(Long id, TourUpdateManagerRequestDTO requestDTO);

    GeneralResponse<List<TourDayManagerDTO>> getTourDays(Long tourId);

    GeneralResponse<TourDayManagerDTO> createTourDay(Long tourId, TourDayManagerCreateRequestDTO requestDTO);

    GeneralResponse<TourDayManagerDTO> updateTourDay(Long tourId, Long dayId, TourDayManagerCreateRequestDTO requestDTO);

    GeneralResponse<String> deleteTourDay(Long tourId, Long dayId);

    GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId);

    GeneralResponse<TourPaxManagerDTO> createTourPax(Long tourId, TourPaxManagerCreateRequestDTO requestDTO);
}
