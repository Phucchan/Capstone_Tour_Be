package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourScheduleCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleOptionsDTO;
import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourThemeOptionDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.UserMapper;
import com.fpt.capstone.tourism.model.enums.ScheduleRepeatType;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.model.enums.TourStatus;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.TourScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourScheduleServiceImpl implements TourScheduleService {

    private final TourManagementRepository tourRepository;
    private final TourPaxRepository tourPaxRepository;
    private final UserRepository userRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final UserMapper userMapper;

    @Override
    public GeneralResponse<List<TourScheduleManagerDTO>> createTourSchedule(Long tourId, TourScheduleCreateRequestDTO requestDTO) {
        if (requestDTO.getDepartureDate().isBefore(LocalDateTime.now())) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.DEPARTURE_DATE_IN_PAST);
        }
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));
        if (tour.getTourStatus() != TourStatus.PUBLISHED) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_NOT_PUBLISHED);
        }
        TourPax tourPax = tourPaxRepository.findById(requestDTO.getTourPaxId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_PAX_NOT_FOUND));
        if (!tourPax.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_PAX_MISMATCH);
        }

        var coordinator = userRepository.findById(requestDTO.getCoordinatorId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        ScheduleRepeatType repeatType = requestDTO.getRepeatType() != null ? requestDTO.getRepeatType() : ScheduleRepeatType.NONE;
        int repeatCount = requestDTO.getRepeatCount() != null ? Math.max(0, requestDTO.getRepeatCount()) : 0;

        List<TourScheduleManagerDTO> result = new ArrayList<>();

        for (int i = 0; i <= repeatCount; i++) {
            LocalDateTime departureDate = requestDTO.getDepartureDate();
            if (i > 0) {
                switch (repeatType) {
                    case WEEKLY:
                        departureDate = departureDate.plusWeeks(i);
                        break;
                    case MONTHLY:
                        departureDate = departureDate.plusMonths(i);
                        break;
                    case YEARLY:
                        departureDate = departureDate.plusYears(i);
                        break;
                    default:
                        break;
                }
            }

            LocalDateTime endDate = departureDate.plusDays(tour.getDurationDays() - 1L);

            TourSchedule schedule = new TourSchedule();
            schedule.setTour(tour);
            schedule.setCoordinator(coordinator);
            schedule.setTourPax(tourPax);
            schedule.setPrice(tourPax.getSellingPrice());
            schedule.setAvailableSeats(tourPax.getMaxQuantity());
            schedule.setDepartureDate(departureDate);
            schedule.setEndDate(endDate);
            schedule.setPublished(false);

            TourSchedule saved = tourScheduleRepository.save(schedule);

            result.add(TourScheduleManagerDTO.builder()
                    .id(saved.getId())
                    .coordinatorId(saved.getCoordinator().getId())
                    .tourPaxId(saved.getTourPax().getId())
                    .departureDate(saved.getDepartureDate())
                    .endDate(saved.getEndDate())
                    .price(saved.getPrice())
                    .build());
        }

        return GeneralResponse.of(result, Constants.Message.SCHEDULE_CREATED_SUCCESS);
    }

    @Override
    public GeneralResponse<TourScheduleOptionsDTO> getScheduleOptions(Long tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        List<UserBasicDTO> coordinatorDtos = userRepository.findByRoleName("SERVICE_COORDINATOR").stream()
                .map(userMapper::toUserBasicDTO)
                .collect(Collectors.toList());

        List<TourPaxManagerDTO> paxDtos = tourPaxRepository.findByTourId(tourId).stream()
                .map(p -> TourPaxManagerDTO.builder()
                        .id(p.getId())
                        .minQuantity(p.getMinQuantity())
                        .maxQuantity(p.getMaxQuantity())
                        .build())
                .collect(Collectors.toList());

        List<TourThemeOptionDTO> themeDtos = tour.getThemes().stream()
                .map(t -> new TourThemeOptionDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());

        UserBasicDTO creator = tour.getCreatedBy() != null ? userMapper.toUserBasicDTO(tour.getCreatedBy()) : null;

        TourScheduleOptionsDTO dto = TourScheduleOptionsDTO.builder()
                .tourId(tour.getId())
                .tourName(tour.getName())
                .tourType(tour.getTourType() != null ? tour.getTourType().name() : null)
                .themes(themeDtos)
                .durationDays(tour.getDurationDays())
                .createdDate(tour.getCreatedAt())
                .createdBy(creator)
                .coordinators(coordinatorDtos)
                .tourPaxes(paxDtos)
                .build();

        return GeneralResponse.of(dto, Constants.Message.GET_SCHEDULE_OPTIONS_SUCCESS);
    }

    @Override
    public GeneralResponse<List<TourScheduleManagerDTO>> getTourSchedules(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        List<TourScheduleManagerDTO> schedules = tourScheduleRepository.findByTourId(tourId)
                .stream()
                .filter(s -> !Boolean.TRUE.equals(s.getDeleted()))
                .map(s -> TourScheduleManagerDTO.builder()
                        .id(s.getId())
                        .coordinatorId(s.getCoordinator() != null ? s.getCoordinator().getId() : null)
                        .tourPaxId(s.getTourPax() != null ? s.getTourPax().getId() : null)
                        .departureDate(s.getDepartureDate())
                        .endDate(s.getEndDate())
                        .price(s.getPrice())
                        .build())
                .collect(Collectors.toList());

        return GeneralResponse.of(schedules);
    }
    @Override
    public GeneralResponse<String> deleteTourSchedule(Long tourId, Long scheduleId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        TourSchedule schedule = tourScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_SCHEDULE_NOT_FOUND));

        if (!schedule.getTour().getId().equals(tourId)) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.SCHEDULE_NOT_BELONG);
        }

        tourScheduleRepository.delete(schedule);

        return GeneralResponse.of(Constants.Message.SCHEDULE_DELETED_SUCCESS);
    }
}