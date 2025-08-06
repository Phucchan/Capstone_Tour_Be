package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.PartnerServiceShortDTO;
import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.*;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourOptionsDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface TourManagementService {

    GeneralResponse<PagingDTO<TourResponseManagerDTO>> getListTours(int page, int size, String keyword,
                                                                    TourType tourType, TourStatus tourStatus);

    GeneralResponse<TourDetailManagerDTO> createTour(TourCreateManagerRequestDTO requestDTO, MultipartFile file);

    GeneralResponse<TourDetailManagerDTO> createTourFromRequest(Long requestId);

    GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO);

    GeneralResponse<TourDetailOptionsDTO> getTourDetail(Long id);

    GeneralResponse<TourDetailManagerDTO> updateTour(Long id, TourUpdateManagerRequestDTO requestDTO, MultipartFile file);

    GeneralResponse<List<TourDayManagerDTO>> getTourDays(Long tourId);

    GeneralResponse<TourDayManagerDTO> createTourDay(Long tourId, TourDayManagerCreateRequestDTO requestDTO);

    GeneralResponse<TourDayManagerDTO> updateTourDay(Long tourId, Long dayId, TourDayManagerCreateRequestDTO requestDTO);

    GeneralResponse<String> deleteTourDay(Long tourId, Long dayId);

    GeneralResponse<TourDayManagerDTO> addServiceToTourDay(Long tourId, Long dayId, Long serviceId);

    GeneralResponse<TourDayManagerDTO> updateServiceInTourDay(Long tourId, Long dayId, Long serviceId, Long newServiceId);

    GeneralResponse<TourDayManagerDTO> removeServiceFromTourDay(Long tourId, Long dayId, Long serviceId);


    GeneralResponse<TourOptionsDTO> getTourOptions();

    GeneralResponse<List<ServiceTypeShortDTO>> getServiceTypes();

    GeneralResponse<List<PartnerServiceShortDTO>> getPartnerServices(Long serviceTypeId);
}

