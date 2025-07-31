package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.ServiceBreakdownDTO;
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

    @Override
    @Transactional
    public GeneralResponse<List<TourPaxFullDTO>> calculatePrices(Long tourId, TourPriceCalculateRequestDTO request) {
        log.info("Bắt đầu tính giá cho tour ID: {}", tourId);
        try {
            getTourOrThrow(tourId);

            // SỬA LỖI: Gọi phương thức mới findByTourIdWithServices để đảm bảo danh sách dịch vụ được tải lên.
            List<TourDay> days = tourDayRepository.findByTourIdWithServices(tourId);

            if (days.isEmpty()) {
                log.warn("Tour ID: {} không có ngày nào được cấu hình. Không thể tính chi phí.", tourId);
            }

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
            log.info("Tour ID: {}. Chi phí tính toán được: fixedCost = {}, perPersonCost = {}", tourId, fixedCost, perPersonCost);

            double profitRate = request.getProfitRate() != null ? request.getProfitRate() / 100d : 0d;
            double extraCost = request.getExtraCost() != null ? request.getExtraCost() : 0d;

            List<TourPax> paxList = tourPaxRepository.findByTourId(tourId);
            if (paxList.isEmpty()) {
                log.warn("Tour ID: {} không có khung giá (pax) nào.", tourId);
            }

            for (TourPax pax : paxList) {
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

    @Override
    public GeneralResponse<List<ServiceBreakdownDTO>> getServiceBreakdown(Long tourId) {
        tourRepository.findById(tourId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Tour not found"));

        List<TourDay> days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tourId);

        // Sử dụng vòng lặp for-each thay vì stream để dễ dàng kiểm tra null và gỡ lỗi
        List<ServiceBreakdownDTO> results = new java.util.ArrayList<>();
        for (TourDay day : days) {
            for (com.fpt.capstone.tourism.model.partner.PartnerService service : day.getServices()) {

                // Bắt đầu kiểm tra null để đảm bảo an toàn
                String serviceTypeName = "N/A";
                if (service.getServiceType() != null) {
                    serviceTypeName = service.getServiceType().getName();
                }

                String partnerName = "N/A";
                String partnerAddress = "N/A";
                if (service.getPartner() != null) {
                    partnerName = service.getPartner().getName();
                    if (service.getPartner().getLocation() != null) {
                        partnerAddress = service.getPartner().getLocation().getName();
                    }
                }
                // Kết thúc kiểm tra null

                results.add(ServiceBreakdownDTO.builder()
                        .dayId(day.getId())
                        .serviceId(service.getId())
                        .dayNumber(day.getDayNumber())
                        .serviceTypeName(serviceTypeName)
                        .partnerName(partnerName)
                        .partnerAddress(partnerAddress)
                        .nettPrice(service.getNettPrice())
                        .sellingPrice(service.getSellingPrice())
                        .build());
            }
        }

        return GeneralResponse.of(results);
    }
}
