package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.ServiceBreakdownDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourDayDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourDetailDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourResponseDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.tourManager.TourDayMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourDetailMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourManagementMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourTheme;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourManagementServiceImpl implements com.fpt.capstone.tourism.service.TourManagementService {


    @Autowired
    private TourManagementRepository tourRepository;

    @Autowired
    private TourManagementMapper tourMapper;

    @Autowired
    private TourDetailMapper tourDetailMapper;

    @Autowired
    private TourThemeRepository tourThemeRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TourDayRepository tourDayRepository;

    @Autowired
    private TourDayMapper tourDayMapper;

    @Autowired
    private PartnerServiceRepository partnerServiceRepository;

    @Autowired
    private TourPaxRepository tourPaxRepository;

    @Override
    public GeneralResponse<List<TourResponseDTO>> getListTours() {
        List<Tour> tours = tourRepository.findAll();
        List<TourResponseDTO> tourResponseDTOs = tours.stream()
                .map(tourMapper::toTourResponseDTO)
                .collect(Collectors.toList());
        return GeneralResponse.of(tourResponseDTOs);
    }




    @Override
    public GeneralResponse<Object> changeStatus(Long id, ChangeStatusDTO changeStatusDTO) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        try {
            TourStatus newStatus = TourStatus.valueOf(changeStatusDTO.getNewStatus());
            tour.setTourStatus(newStatus);
            tourRepository.save(tour);
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Invalid tour status: " + changeStatusDTO.getNewStatus(), e);
        }

        return GeneralResponse.of((Object) null, "Status updated successfully");

    }

    @Override
    public GeneralResponse<Object> getTourDetail(Long id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        TourDetailDTO dto = tourDetailMapper.toDTO(tour);
        return GeneralResponse.of(dto);
    }

    @Override
    public GeneralResponse<TourDetailDTO> updateTour(Long id, TourUpdateRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        if (requestDTO.getThumbnailUrl() != null) {
            tour.setThumbnailUrl(requestDTO.getThumbnailUrl());
        }
        if (requestDTO.getTourThemeId() != null) {
            TourTheme theme = tourThemeRepository.findById(requestDTO.getTourThemeId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour theme not found"));
            tour.setTourTheme(theme);
        }
        if (requestDTO.getDepartLocationId() != null) {
            Location depart = locationRepository.findById(requestDTO.getDepartLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            tour.setDepartLocation(depart);
        }
        if (requestDTO.getDestinationLocationId() != null) {
            Location dest = locationRepository.findById(requestDTO.getDestinationLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            tour.setDestinationLocation(dest);
        }
        if (requestDTO.getDurationDays() != null) {
            tour.setDurationDays(requestDTO.getDurationDays());
        }
        if (requestDTO.getDescription() != null) {
            tour.setDescription(requestDTO.getDescription());
        }

        Tour saved = tourRepository.save(tour);
        TourDetailDTO dto = tourDetailMapper.toDTO(saved);
        return GeneralResponse.of(dto, "Tour updated successfully");
    }
    @Override
    public GeneralResponse<List<TourDayDTO>> getTourDays(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        List<TourDay> days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        List<TourDayDTO> dtos = days.stream().map(tourDayMapper::toDTO).collect(Collectors.toList());
        return GeneralResponse.of(dtos);
    }

    @Override
    public GeneralResponse<TourDayDTO> createTourDay(Long tourId, TourDayCreateRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        int nextDayNumber = 1;
        List<TourDay> existing = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        if (!existing.isEmpty()) {
            nextDayNumber = existing.get(existing.size() - 1).getDayNumber() + 1;
        }

        TourDay day = new TourDay();
        day.setTour(tour);
        day.setDayNumber(nextDayNumber);
        day.setTitle(requestDTO.getTitle());
        day.setDescription(requestDTO.getDescription());

        if (requestDTO.getLocationId() != null) {
            Location location = locationRepository.findById(requestDTO.getLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            day.setLocation(location);
        }

        if (requestDTO.getServiceIds() != null && !requestDTO.getServiceIds().isEmpty()) {
            day.setServices(partnerServiceRepository.findAllById(requestDTO.getServiceIds()));
        }

        TourDay saved = tourDayRepository.save(day);
        return GeneralResponse.of(tourDayMapper.toDTO(saved), "Tour day created successfully");
    }

    @Override
    public GeneralResponse<TourDayDTO> updateTourDay(Long tourId, Long dayId, TourDayCreateRequestDTO requestDTO) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        if (requestDTO.getTitle() != null) day.setTitle(requestDTO.getTitle());
        if (requestDTO.getDescription() != null) day.setDescription(requestDTO.getDescription());

        if (requestDTO.getLocationId() != null) {
            Location location = locationRepository.findById(requestDTO.getLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            day.setLocation(location);
        }

        if (requestDTO.getServiceIds() != null) {
            day.setServices(partnerServiceRepository.findAllById(requestDTO.getServiceIds()));
        }

        TourDay saved = tourDayRepository.save(day);
        return GeneralResponse.of(tourDayMapper.toDTO(saved), Constants.Message.TOUR_DAY_UPDATED_SUCCESS);
    }

    @Override
    public GeneralResponse<String> deleteTourDay(Long tourId, Long dayId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        day.softDelete();
        tourDayRepository.save(day);
        return GeneralResponse.of(Constants.Message.TOUR_DAY_DELETED_SUCCESS);
    }

    @Override
    public GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        List<TourDay> days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        List<ServiceBreakdownDTO> results = days.stream()
                .flatMap(day -> day.getServices().stream().map(service -> ServiceBreakdownDTO.builder()
                        .dayNumber(day.getDayNumber())
                        .serviceTypeName(service.getServiceType().getName())
                        .partnerName(service.getPartner().getName())
                        .partnerAddress(service.getPartner().getLocation().getName())
                        .nettPrice(service.getNettPrice())
                        .sellingPrice(service.getSellingPrice())
                        .build()))
                .collect(Collectors.toList());

        return GeneralResponse.of(results);
    }

    @Override
    public GeneralResponse<TourPaxDTO> createTourPax(Long tourId, TourPaxCreateRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourPax pax = new TourPax();
        pax.setTour(tour);
        pax.setMinQuantity(requestDTO.getMinQuantity());
        pax.setMaxQuantity(requestDTO.getMaxQuantity());
        TourPax saved = tourPaxRepository.save(pax);

        TourPaxDTO dto = TourPaxDTO.builder()
                .id(saved.getId())
                .minQuantity(saved.getMinQuantity())
                .maxQuantity(saved.getMaxQuantity())
                .build();

        return GeneralResponse.of(dto, Constants.Message.PAX_CONFIG_CREATE_SUCCESS);
    }

}
