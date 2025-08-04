package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.ServiceBreakdownDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourCostSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxFullDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.enums.CostType;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourPax;
import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import com.fpt.capstone.tourism.service.TourPaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPriceCalculateRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.fpt.capstone.tourism.constants.Constants.Message.*;

@Service
@RequiredArgsConstructor
public class TourPaxServiceImpl implements TourPaxService {

    private final TourRepository tourRepository;
    private final TourPaxRepository tourPaxRepository;
    private final TourDayRepository tourDayRepository;
    private static final Logger log = LoggerFactory.getLogger(TourPaxServiceImpl.class);


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
            // Thêm validation chống trùng lặp
            validatePaxRange(tourId, null, request.getMinQuantity(), request.getMaxQuantity());

            TourPax pax = TourPax.builder()
                    .tour(tour)
                    .minQuantity(request.getMinQuantity())
                    .maxQuantity(request.getMaxQuantity())
                    .fixedPrice(request.getFixedPrice() == null ? 0d : request.getFixedPrice())
                    .sellingPrice(request.getSellingPrice() == null ? 0d : request.getSellingPrice())
                    .manualPrice(request.isManualPrice())
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
            // Thêm validation chống trùng lặp
            validatePaxRange(tourId, paxId, minQty, maxQty);

            pax.setMinQuantity(minQty);
            pax.setMaxQuantity(maxQty);
            if (request.getFixedPrice() != null) pax.setFixedPrice(request.getFixedPrice());
            if (request.getSellingPrice() != null) pax.setSellingPrice(request.getSellingPrice());
            if (request.getManualPrice() != null) pax.setManualPrice(request.getManualPrice());

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

    @Override
    @Transactional
    public GeneralResponse<List<TourPaxFullDTO>> calculatePrices(Long tourId, TourPriceCalculateRequestDTO request) {
        log.info("Bắt đầu tính giá cho tour ID: {}", tourId);
        try {
            TourCostSummaryDTO costSummary = getTourCostSummary(tourId).getData();
            double fixedCost = costSummary.getTotalFixedCost();
            double perPersonCost = costSummary.getTotalPerPersonCost();
            log.info("Tour ID: {}. Chi phí tính toán được: fixedCost = {}, perPersonCost = {}", tourId, fixedCost, perPersonCost);

            double profitRate = request.getProfitRate() != null ? request.getProfitRate() / 100d : 0d;
            double extraCost = request.getExtraCost() != null ? request.getExtraCost() : 0d;

            List<TourPax> paxList = tourPaxRepository.findByTourIdAndDeletedIsFalse(tourId);
            if (paxList.isEmpty()) {
                log.warn("Tour ID: {} không có khung giá (pax) nào.", tourId);
            }

            for (TourPax pax : paxList) {
                // --- LOGIC MỚI: Chỉ tính toán nếu giá không được nhập thủ công ---
                if (pax.isManualPrice()) {
                    log.info(" -> Bỏ qua Pax ID {} vì giá được nhập thủ công.", pax.getId());
                    continue;
                }

                int paxCount = pax.getMaxQuantity();
                if (paxCount == 0) {
                    log.error("Lỗi nghiêm trọng: maxQuantity của Pax ID {} bằng 0. Bỏ qua.", pax.getId());
                    continue;
                }

                double totalCostPerPax = (fixedCost / paxCount) + perPersonCost;
                pax.setFixedPrice(totalCostPerPax);

                double sellingPrice = totalCostPerPax * (1 + profitRate) + extraCost;
                pax.setSellingPrice(sellingPrice);
                log.info(" -> Pax ID {}: ({} - {} khách): Giá vốn = {}, Giá bán = {}", pax.getId(), pax.getMinQuantity(), pax.getMaxQuantity(), totalCostPerPax, sellingPrice);
            }

            tourPaxRepository.saveAll(paxList);
            List<TourPaxFullDTO> dtos = paxList.stream().map(this::toDTO).collect(Collectors.toList());
            log.info("Hoàn tất tính giá cho tour ID: {}", tourId);
            return new GeneralResponse<>(HttpStatus.OK.value(), CONFIG_UPDATED, dtos);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Lỗi không xác định khi tính giá cho tour ID: " + tourId, ex);
            throw BusinessException.of(HttpStatus.INTERNAL_SERVER_ERROR, FAILED_TO_UPDATE_PAX_CONFIGURATION, ex);
        }
    }




    @Override
    public GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId) {
        getTourOrThrow(tourId);
        List<TourDay> days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);

        List<ServiceBreakdownDTO> results = new java.util.ArrayList<>();
        for (TourDay day : days) {
            for (PartnerService service : day.getServices()) {
                if (Boolean.TRUE.equals(service.getDeleted())) continue;

                results.add(ServiceBreakdownDTO.builder()
                        .dayId(day.getId())
                        .serviceId(service.getId())
                        .dayNumber(day.getDayNumber())
                        .serviceTypeName(service.getServiceType() != null ? service.getServiceType().getName() : "N/A")
                        .partnerName(service.getPartner() != null ? service.getPartner().getName() : "N/A")
                        .partnerAddress(service.getPartner() != null && service.getPartner().getLocation() != null ? service.getPartner().getLocation().getName() : "N/A")
                        .nettPrice(service.getNettPrice())
                        .sellingPrice(service.getSellingPrice())
                        .costType(service.getCostType()) // Cập nhật trường mới
                        .build());
            }
        }
        return GeneralResponse.of(results);
    }

    @Override
    public GeneralResponse<TourCostSummaryDTO> getTourCostSummary(Long tourId) {
        getTourOrThrow(tourId);
        List<TourDay> days = tourDayRepository.findByTourIdWithServices(tourId);

        double fixedCost = 0d;
        double perPersonCost = 0d;

        for (TourDay day : days) {
            for (PartnerService service : day.getServices()) {
                if (Boolean.TRUE.equals(service.getDeleted())) continue;
                if (service.getCostType() == CostType.FIXED) {
                    fixedCost += service.getNettPrice();
                } else if (service.getCostType() == CostType.PER_PERSON) {
                    perPersonCost += service.getNettPrice();
                }
            }
        }
        TourCostSummaryDTO summary = new TourCostSummaryDTO(fixedCost, perPersonCost);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Cost summary loaded", summary);
    }

    private TourPaxFullDTO toDTO(TourPax pax) {
        return TourPaxFullDTO.builder()
                .id(pax.getId())
                .tourId(pax.getTour().getId())
                .minQuantity(pax.getMinQuantity())
                .maxQuantity(pax.getMaxQuantity())
                .fixedPrice(pax.getFixedPrice())
                .sellingPrice(pax.getSellingPrice())
                .manualPrice(pax.isManualPrice()) // Cập nhật trường mới
                .isDeleted(Boolean.TRUE.equals(pax.getDeleted()))
                .build();
    }

    private void validatePaxRange(Long tourId, Long currentPaxId, int minQty, int maxQty) {
        List<TourPax> existingPaxes = tourPaxRepository.findByTourIdAndDeletedIsFalse(tourId);
        for (TourPax pax : existingPaxes) {
            // Bỏ qua chính nó khi đang cập nhật
            if (pax.getId().equals(currentPaxId)) {
                continue;
            }
            // Kiểm tra sự chồng chéo
            boolean isOverlapping = (minQty >= pax.getMinQuantity() && minQty <= pax.getMaxQuantity()) ||
                    (maxQty >= pax.getMinQuantity() && maxQty <= pax.getMaxQuantity()) ||
                    (minQty < pax.getMinQuantity() && maxQty > pax.getMaxQuantity());
            if (isOverlapping) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, "Khoảng khách bị trùng với một khoảng đã có.");
            }
        }
    }
}
