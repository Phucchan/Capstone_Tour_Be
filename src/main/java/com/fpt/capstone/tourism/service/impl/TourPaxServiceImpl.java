package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxFullDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import com.fpt.capstone.tourism.service.TourPaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.fpt.capstone.tourism.constants.Constants.Message.*;

@Service
@RequiredArgsConstructor
public class TourPaxServiceImpl implements TourPaxService {

    private final TourRepository tourRepository;
    private final TourPaxRepository tourPaxRepository;

    private Tour getTourOrThrow(Long tourId) {
        return tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND,
                        TOUR_NOT_FOUND + " id: " + tourId));
    }

    private TourPax getPaxOrThrow(Long paxId) {
        return tourPaxRepository.findById(paxId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND,
                        PAX_CONFIG_NOT_FOUND + " id: " + paxId));
    }

    @Override
    public GeneralResponse<List<TourPaxFullDTO>> getTourPaxConfigurations(Long tourId) {
        try {
            getTourOrThrow(tourId);
            List<TourPaxFullDTO> dtos = tourPaxRepository.findByTourId(tourId)
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
            return new GeneralResponse<>(HttpStatus.OK.value(), PAX_CONFIG_LOAD_SUCCESS, dtos);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw BusinessException.of(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_RETRIEVE_PAX_CONFIGURATION, ex);
        }
    }

    @Override
    public GeneralResponse<TourPaxFullDTO> getTourPaxConfiguration(Long tourId, Long paxId) {
        try {
            getTourOrThrow(tourId);
            TourPax pax = getPaxOrThrow(paxId);
            if (!pax.getTour().getId().equals(tourId)) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, PAX_CONFIG_NOT_ASSOCIATED);
            }
            return new GeneralResponse<>(HttpStatus.OK.value(), PAX_CONFIG_LOAD_SUCCESS, toDTO(pax));
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw BusinessException.of(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_RETRIEVE_PAX_CONFIGURATION, ex);
        }
    }

    @Override
    @Transactional
    public GeneralResponse<TourPaxFullDTO> createTourPaxConfiguration(Long tourId, TourPaxCreateRequestDTO request) {
        try {
            Tour tour = getTourOrThrow(tourId);
            if (request.getMinQuantity() > request.getMaxQuantity()) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, PAX_CONFIG_INVALID_RANGE);
            }
            TourPax pax = TourPax.builder()
                    .tour(tour)
                    .minQuantity(request.getMinQuantity())
                    .maxQuantity(request.getMaxQuantity())
                    .fixedPrice(request.getFixedPrice() == null ? 0d : request.getFixedPrice())
                    .extraHotelCost(request.getExtraHotelCost() == null ? 0d : request.getExtraHotelCost())
                    .sellingPrice(request.getSellingPrice() == null ? 0d : request.getSellingPrice())
                    .build();
            pax = tourPaxRepository.save(pax);
            return new GeneralResponse<>(HttpStatus.CREATED.value(), PAX_CONFIG_CREATE_SUCCESS, toDTO(pax));
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw BusinessException.of(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_CREATE_PAX_CONFIGURATION, ex);
        }
    }

    @Override
    @Transactional
    public GeneralResponse<TourPaxFullDTO> updateTourPaxConfiguration(Long tourId, Long paxId, TourPaxUpdateRequestDTO request) {
        try {
            getTourOrThrow(tourId);
            TourPax pax = getPaxOrThrow(paxId);
            if (!pax.getTour().getId().equals(tourId)) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, PAX_CONFIG_NOT_ASSOCIATED);
            }
            Integer minQty = request.getMinQuantity() != null ? request.getMinQuantity() : pax.getMinQuantity();
            Integer maxQty = request.getMaxQuantity() != null ? request.getMaxQuantity() : pax.getMaxQuantity();

            if (minQty > maxQty) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, PAX_CONFIG_INVALID_RANGE);
            }
            pax.setMinQuantity(minQty);
            pax.setMaxQuantity(maxQty);
            if (request.getFixedPrice() != null) {
                pax.setFixedPrice(request.getFixedPrice());
            }
            if (request.getExtraHotelCost() != null) {
                pax.setExtraHotelCost(request.getExtraHotelCost());
            }
            if (request.getSellingPrice() != null) {
                pax.setSellingPrice(request.getSellingPrice());
            }
            pax = tourPaxRepository.save(pax);
            return new GeneralResponse<>(HttpStatus.OK.value(), PAX_CONFIG_UPDATE_SUCCESS, toDTO(pax));
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw BusinessException.of(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_UPDATE_PAX_CONFIGURATION, ex);
        }
    }

    @Override
    @Transactional
    public GeneralResponse<String> deleteTourPaxConfiguration(Long tourId, Long paxId) {
        try {
            getTourOrThrow(tourId);
            TourPax pax = getPaxOrThrow(paxId);
            if (!pax.getTour().getId().equals(tourId)) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, PAX_CONFIG_NOT_ASSOCIATED);
            }
            pax.softDelete();
            tourPaxRepository.save(pax);
            return new GeneralResponse<>(HttpStatus.OK.value(), PAX_CONFIG_DELETE_SUCCESS, PAX_CONFIG_DELETE_SUCCESS);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw BusinessException.of(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_DELETE_PAX_CONFIGURATION, ex);
        }
    }

    private TourPaxFullDTO toDTO(TourPax pax) {
        return TourPaxFullDTO.builder()
                .id(pax.getId())
                .tourId(pax.getTour().getId())
                .minQuantity(pax.getMinQuantity())
                .maxQuantity(pax.getMaxQuantity())
                .fixedPrice(pax.getFixedPrice())
                .extraHotelCost(pax.getExtraHotelCost())
                .sellingPrice(pax.getSellingPrice())
                .isDeleted(Boolean.TRUE.equals(pax.getDeleted()))
                .build();
    }
}
