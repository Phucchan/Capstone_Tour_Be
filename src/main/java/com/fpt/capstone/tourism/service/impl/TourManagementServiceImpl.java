package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourCreateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourDayManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourOptionsDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourThemeOptionDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourUpdateManagerRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.*;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.tourManager.TourDayManagerMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourDetailManagerMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourManagementMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.tour.TourTheme;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
import com.fpt.capstone.tourism.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.fpt.capstone.tourism.specifications.TourSpecification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TourManagementServiceImpl implements com.fpt.capstone.tourism.service.TourManagementService {


    @Autowired
    private TourManagementRepository tourRepository;

    @Autowired
    private TourManagementMapper tourMapper;

    @Autowired
    private TourDetailManagerMapper tourDetailManagerMapper;

    @Autowired
    private LocationService locationService;

    @Autowired
    private TourThemeRepository tourThemeRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TourDayRepository tourDayRepository;

    @Autowired
    private TourDayManagerMapper tourDayManagerMapper;

    @Autowired
    private TourPaxRepository tourPaxRepository;

    @Autowired
    private PartnerServiceRepository partnerServiceRepository;

    @Override
    public GeneralResponse<PagingDTO<TourResponseManagerDTO>> getListTours(int page, int size, String keyword,
                                                                           TourType tourType, TourStatus tourStatus) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        Specification<Tour> spec = Specification.where(null);
        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(TourSpecification.hasNameLike(keyword));
        }
        if (tourType != null) {
            spec = spec.and(TourSpecification.hasTourType(tourType));
        }
        if (tourStatus != null) {
            spec = spec.and(TourSpecification.hasTourStatus(tourStatus));
        }
        Page<Tour> tours = tourRepository.findAll(spec, pageable);

        List<TourResponseManagerDTO> tourResponseDTOs = tours.getContent().stream()
                .map(tourMapper::toTourResponseDTO)
                .collect(Collectors.toList());
        PagingDTO<TourResponseManagerDTO> pagingDTO = PagingDTO.<TourResponseManagerDTO>builder()
                .page(tours.getNumber())
                .size(tours.getSize())
                .total(tours.getTotalElements())
                .items(tourResponseDTOs)
                .build();
        return GeneralResponse.of(pagingDTO);
    }

    @Override
    public GeneralResponse<TourDetailManagerDTO> createTour(TourCreateManagerRequestDTO requestDTO) {
        Tour tour = new Tour();
        tour.setCode(requestDTO.getCode());
        tour.setName(requestDTO.getName());
        tour.setThumbnailUrl(requestDTO.getThumbnailUrl());

        tour.setTourType(TourType.FIXED);
        tour.setTourStatus(TourStatus.DRAFT);
        if (requestDTO.getDepartLocationId() != null) {
            Location depart = locationRepository.findById(requestDTO.getDepartLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            tour.setDepartLocation(depart);
        }

        if (requestDTO.getDurationDays() != null) {
            tour.setDurationDays(requestDTO.getDurationDays());
        }

        tour.setDescription(requestDTO.getDescription());
        tour.setTourStatus(TourStatus.DRAFT);

        Tour saved = tourRepository.save(tour);
        if (requestDTO.getTourThemeIds() != null) {
            for (Long themeId : requestDTO.getTourThemeIds()) {
                TourTheme theme = tourThemeRepository.findById(themeId)
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour theme not found"));
                theme.setTour(saved);
                tourThemeRepository.save(theme);
            }
        }

        // Create tour days for multiple destinations if provided
        if (requestDTO.getDestinationLocationIds() != null) {
            int dayNumber = 1;
            for (Long destId : requestDTO.getDestinationLocationIds()) {
                Location dest = locationRepository.findById(destId)
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
                TourDay day = new TourDay();
                day.setTour(saved);
                day.setDayNumber(dayNumber++);
                day.setLocation(dest);
                tourDayRepository.save(day);
            }
            saved.setDurationDays(dayNumber - 1);
        }
        TourDetailManagerDTO dto = tourDetailManagerMapper.toDTO(saved);
        List<String> themeNames = tourThemeRepository.findByTourId(saved.getId())
                .stream()
                .map(TourTheme::getName)
                .collect(Collectors.toList());
        dto.setTourThemeNames(themeNames);

        List<String> destNames = tourDayRepository.findByTourIdOrderByDayNumberAsc(saved.getId())
                .stream()
                .map(day -> day.getLocation() != null ? day.getLocation().getName() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        dto.setDestinationLocationNames(destNames);
        return GeneralResponse.of(dto, "Tour created successfully");
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
        TourDetailManagerDTO dto = tourDetailManagerMapper.toDTO(tour);
        List<String> themeNames = tourThemeRepository.findByTourId(tour.getId())
                .stream()
                .map(TourTheme::getName)
                .collect(Collectors.toList());
        dto.setTourThemeNames(themeNames);

        List<String> destNames = tourDayRepository.findByTourIdOrderByDayNumberAsc(tour.getId())
                .stream()
                .map(day -> day.getLocation() != null ? day.getLocation().getName() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        dto.setDestinationLocationNames(destNames);
        return GeneralResponse.of(dto);
    }

    @Override
    public GeneralResponse<TourDetailManagerDTO> updateTour(Long id, TourUpdateManagerRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        if (requestDTO.getCode() != null) {
            tour.setCode(requestDTO.getCode());
        }
        if (requestDTO.getName() != null) {
            tour.setName(requestDTO.getName());
        }
        if (requestDTO.getThumbnailUrl() != null) {
            tour.setThumbnailUrl(requestDTO.getThumbnailUrl());
        }
        if (requestDTO.getTourThemeIds() != null) {
            for (Long themeId : requestDTO.getTourThemeIds()) {
                TourTheme theme = tourThemeRepository.findById(themeId)
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour theme not found"));
                theme.setTour(tour);
                tourThemeRepository.save(theme);
            }
        }
        if (requestDTO.getDepartLocationId() != null) {
            Location depart = locationRepository.findById(requestDTO.getDepartLocationId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
            tour.setDepartLocation(depart);
        }
        if (requestDTO.getDurationDays() != null) {
            tour.setDurationDays(requestDTO.getDurationDays());
        }
        if (requestDTO.getDestinationLocationIds() != null) {
            List<TourDay> existing = tourDayRepository.findByTourIdOrderByDayNumberAsc(id);
            for (TourDay day : existing) {
                day.softDelete();
                tourDayRepository.save(day);
            }
            int dayNumber = 1;
            for (Long destId : requestDTO.getDestinationLocationIds()) {
                Location dest = locationRepository.findById(destId)
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Location not found"));
                TourDay day = new TourDay();
                day.setTour(tour);
                day.setDayNumber(dayNumber++);
                day.setLocation(dest);
                tourDayRepository.save(day);
            }
            tour.setDurationDays(dayNumber - 1);
        }
        if (requestDTO.getDescription() != null) {
            tour.setDescription(requestDTO.getDescription());
        }

        Tour saved = tourRepository.save(tour);
        TourDetailManagerDTO dto = tourDetailManagerMapper.toDTO(saved);
        List<String> themeNames = tourThemeRepository.findByTourId(saved.getId())
                .stream()
                .map(TourTheme::getName)
                .collect(Collectors.toList());
        dto.setTourThemeNames(themeNames);

        List<String> destNames = tourDayRepository.findByTourIdOrderByDayNumberAsc(saved.getId())
                .stream()
                .map(day -> day.getLocation() != null ? day.getLocation().getName() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        dto.setDestinationLocationNames(destNames);
        return GeneralResponse.of(dto, "Tour updated successfully");
    }

    @Override
    public GeneralResponse<List<TourDayManagerDTO>> getTourDays(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        List<TourDay> days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);
        List<TourDayManagerDTO> dtos = days.stream().map(tourDayManagerMapper::toDTO).collect(Collectors.toList());
        return GeneralResponse.of(dtos);
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> createTourDay(Long tourId, TourDayManagerCreateRequestDTO requestDTO) {
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
        return GeneralResponse.of(tourDayManagerMapper.toDTO(saved), "Tour day created successfully");
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> updateTourDay(Long tourId, Long dayId, TourDayManagerCreateRequestDTO requestDTO) {
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
        return GeneralResponse.of(tourDayManagerMapper.toDTO(saved), Constants.Message.TOUR_DAY_UPDATED_SUCCESS);
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
    public GeneralResponse<TourDayManagerDTO> addServiceToTourDay(Long tourId, Long dayId, Long serviceId) {
        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        PartnerService service = partnerServiceRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (day.getServices().contains(service)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_ALREADY_EXISTS);
        }

        day.getServices().add(service);
        TourDay saved = tourDayRepository.save(day);

        TourDayManagerDTO dto = tourDayManagerMapper.toDTO(saved);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DAY_SERVICE_ADDED_SUCCESS);
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> updateServiceInTourDay(Long tourId, Long dayId, Long serviceId, Long newServiceId) {
        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        PartnerService oldService = partnerServiceRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (!day.getServices().contains(oldService)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_NOT_ASSOCIATED);
        }

        PartnerService newService = partnerServiceRepository.findById(newServiceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (day.getServices().contains(newService) && !newService.getId().equals(oldService.getId())) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_ALREADY_EXISTS);
        }

        day.getServices().remove(oldService);
        day.getServices().add(newService);
        TourDay saved = tourDayRepository.save(day);

        TourDayManagerDTO dto = tourDayManagerMapper.toDTO(saved);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DAY_SERVICE_UPDATED_SUCCESS);
    }

    @Override
    public GeneralResponse<TourDayManagerDTO> removeServiceFromTourDay(Long tourId, Long dayId, Long serviceId) {
        TourDay day = tourDayRepository.findById(dayId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DAY_NOT_FOUND));

        if (!day.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DAY_NOT_BELONG);
        }

        PartnerService service = partnerServiceRepository.findById(serviceId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.SERVICE_NOT_FOUND));

        if (!day.getServices().contains(service)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SERVICE_NOT_ASSOCIATED);
        }

        day.getServices().remove(service);
        TourDay saved = tourDayRepository.save(day);

        TourDayManagerDTO dto = tourDayManagerMapper.toDTO(saved);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DAY_SERVICE_REMOVED_SUCCESS);
    }


    @Override
    public GeneralResponse<TourPaxManagerDTO> createTourPax(Long tourId, TourPaxManagerCreateRequestDTO requestDTO) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        if (requestDTO.getMinQuantity() > requestDTO.getMaxQuantity()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_INVALID_RANGE);
        }
        TourPax pax = new TourPax();
        pax.setTour(tour);
        pax.setMinQuantity(requestDTO.getMinQuantity());
        pax.setMaxQuantity(requestDTO.getMaxQuantity());
        TourPax saved = tourPaxRepository.save(pax);

        TourPaxManagerDTO dto = TourPaxManagerDTO.builder()
                .id(saved.getId())
                .minQuantity(saved.getMinQuantity())
                .maxQuantity(saved.getMaxQuantity())
                .build();

        return GeneralResponse.of(dto, Constants.Message.PAX_CONFIG_CREATE_SUCCESS);
    }

    @Override
    public GeneralResponse<TourPaxManagerDTO> getTourPax(Long tourId, Long paxId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourPax pax = tourPaxRepository.findById(paxId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.PAX_CONFIG_NOT_FOUND));

        if (!pax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_NOT_ASSOCIATED);
        }

        TourPaxManagerDTO dto = TourPaxManagerDTO.builder()
                .id(pax.getId())
                .minQuantity(pax.getMinQuantity())
                .maxQuantity(pax.getMaxQuantity())
                .build();

        return GeneralResponse.of(dto, Constants.Message.PAX_CONFIG_LOAD_SUCCESS);
    }

    @Override
    public GeneralResponse<TourPaxManagerDTO> updateTourPax(Long tourId, Long paxId, TourPaxManagerCreateRequestDTO requestDTO) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourPax pax = tourPaxRepository.findById(paxId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.PAX_CONFIG_NOT_FOUND));

        if (!pax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_NOT_ASSOCIATED);
        }

        if (requestDTO.getMinQuantity() > requestDTO.getMaxQuantity()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_INVALID_RANGE);
        }

        pax.setMinQuantity(requestDTO.getMinQuantity());
        pax.setMaxQuantity(requestDTO.getMaxQuantity());
        TourPax saved = tourPaxRepository.save(pax);

        TourPaxManagerDTO dto = TourPaxManagerDTO.builder()
                .id(saved.getId())
                .minQuantity(saved.getMinQuantity())
                .maxQuantity(saved.getMaxQuantity())
                .build();

        return GeneralResponse.of(dto, Constants.Message.PAX_CONFIG_UPDATE_SUCCESS);
    }

    @Override
    public GeneralResponse<String> deleteTourPax(Long tourId, Long paxId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourPax pax = tourPaxRepository.findById(paxId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.PAX_CONFIG_NOT_FOUND));

        if (!pax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.PAX_CONFIG_NOT_ASSOCIATED);
        }

        pax.softDelete();
        tourPaxRepository.save(pax);
        return GeneralResponse.of(Constants.Message.PAX_CONFIG_DELETE_SUCCESS);
    }
    @Override
    public GeneralResponse<TourOptionsDTO> getTourOptions() {
        List<TourThemeOptionDTO> themes = tourThemeRepository.findAll().stream()
                .map(t -> TourThemeOptionDTO.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .build())
                .collect(Collectors.toList());

        List<LocationDTO> departures = locationService.getAllDepartures();
        List<LocationDTO> destinations = locationService.getAllDestinations();

        TourOptionsDTO dto = TourOptionsDTO.builder()
                .themes(themes)
                .departures(departures)
                .destinations(destinations)
                .build();
        return GeneralResponse.of(dto);
    }


}
