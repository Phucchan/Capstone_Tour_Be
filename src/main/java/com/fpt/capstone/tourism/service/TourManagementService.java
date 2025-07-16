package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourCreateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourOptionsDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TourManagementService {

    GeneralResponse<PagingDTO<TourResponseManagerDTO>> getListTours(int page, int size, String keyword,
                                                                    TourType tourType, TourStatus tourStatus);

    GeneralResponse<TourDetailManagerDTO> createTour(TourCreateManagerRequestDTO requestDTO);

    public GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO);

    GeneralResponse<Object> getTourDetail(Long id);

    GeneralResponse<TourDetailManagerDTO> updateTour(Long id, TourUpdateManagerRequestDTO requestDTO);

    GeneralResponse<List<TourDayManagerDTO>> getTourDays(Long tourId);

    GeneralResponse<TourDayManagerDTO> createTourDay(Long tourId, TourDayManagerCreateRequestDTO requestDTO);

    GeneralResponse<TourDayManagerDTO> updateTourDay(Long tourId, Long dayId, TourDayManagerCreateRequestDTO requestDTO);

    GeneralResponse<String> deleteTourDay(Long tourId, Long dayId);

    GeneralResponse<TourDayManagerDTO> addServiceToTourDay(Long tourId, Long dayId, Long serviceId);

    GeneralResponse<TourDayManagerDTO> updateServiceInTourDay(Long tourId, Long dayId, Long serviceId, Long newServiceId);

    GeneralResponse<TourDayManagerDTO> removeServiceFromTourDay(Long tourId, Long dayId, Long serviceId);

    GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId);

    GeneralResponse<TourPaxManagerDTO> createTourPax(Long tourId, TourPaxManagerCreateRequestDTO requestDTO);

    GeneralResponse<TourPaxManagerDTO> getTourPax(Long tourId, Long paxId);

    GeneralResponse<TourPaxManagerDTO> updateTourPax(Long tourId, Long paxId, TourPaxManagerCreateRequestDTO requestDTO);

    GeneralResponse<String> deleteTourPax(Long tourId, Long paxId);

    GeneralResponse<TourOptionsDTO> getTourOptions();
}
