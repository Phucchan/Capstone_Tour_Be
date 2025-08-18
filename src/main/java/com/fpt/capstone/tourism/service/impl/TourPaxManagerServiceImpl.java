package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxManagerCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxManagerDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.service.TourPaxManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourPaxManagerServiceImpl implements TourPaxManagerService {

    private final TourManagementRepository tourRepository;
    private final TourPaxRepository tourPaxRepository;

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
                .sellingPrice(saved.getSellingPrice())
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
                .sellingPrice(pax.getSellingPrice())
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
}