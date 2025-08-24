package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.TourDiscountDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.TourDiscountRequestDTO;
import com.fpt.capstone.tourism.dto.response.TourDiscountSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourResponseManagerDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourDiscountMapper;
import com.fpt.capstone.tourism.mapper.tourManager.TourManagementMapper;
import com.fpt.capstone.tourism.model.tour.TourDiscount;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import com.fpt.capstone.tourism.repository.tour.TourDiscountRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.service.TourDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TourDiscountServiceImpl implements TourDiscountService {

    private final TourDiscountRepository tourDiscountRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final TourDiscountMapper tourDiscountMapper;
    private final TourManagementRepository tourRepository;
    private final TourManagementMapper tourManagementMapper;


    @Override
    @Transactional
    public GeneralResponse<TourDiscountDTO> createDiscount(TourDiscountRequestDTO requestDTO) {
        TourSchedule schedule = tourScheduleRepository.findById(requestDTO.getScheduleId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_SCHEDULE_NOT_FOUND));

        // Perform all validations
        validateDiscountRequest(requestDTO, schedule, null);

        TourDiscount discount = TourDiscount.builder()
                .tourSchedule(schedule)
                .discountPercent(requestDTO.getDiscountPercent())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .build();
        discount.setDeleted(false);
        TourDiscount saved = tourDiscountRepository.save(discount);
        TourDiscountDTO dto = tourDiscountMapper.toDTO(saved);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DISCOUNT_CREATE_SUCCESS);
    }

    @Override
    @Transactional
    public GeneralResponse<TourDiscountDTO> updateDiscount(Long id, TourDiscountRequestDTO requestDTO) {
        TourDiscount existingDiscount = tourDiscountRepository.findById(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DISCOUNT_NOT_FOUND));

        TourSchedule schedule = tourScheduleRepository.findById(requestDTO.getScheduleId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_SCHEDULE_NOT_FOUND));

        // Perform all validations, passing the current discount ID to exclude it from conflict checks
        validateDiscountRequest(requestDTO, schedule, id);

        // Update fields
        existingDiscount.setTourSchedule(schedule);
        existingDiscount.setDiscountPercent(requestDTO.getDiscountPercent());
        existingDiscount.setStartDate(requestDTO.getStartDate());
        existingDiscount.setEndDate(requestDTO.getEndDate());

        TourDiscount updated = tourDiscountRepository.save(existingDiscount);
        TourDiscountDTO dto = tourDiscountMapper.toDTO(updated);
        return GeneralResponse.of(dto, Constants.Message.TOUR_DISCOUNT_UPDATE_SUCCESS);
    }

    @Override
    @Transactional
    public GeneralResponse<Void> deleteDiscount(Long id) {
        TourDiscount discount = tourDiscountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DISCOUNT_NOT_FOUND));

        discount.setDeleted(true);
        tourDiscountRepository.save(discount);

        return GeneralResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message(Constants.Message.TOUR_DISCOUNT_DELETE_SUCCESS)
                .data(null)
                .build();
    }

    @Override
    public GeneralResponse<TourDiscountDTO> getDiscountById(Long id) {
        TourDiscount discount = tourDiscountRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_DISCOUNT_NOT_FOUND));
        return GeneralResponse.of(tourDiscountMapper.toDTO(discount));
    }

    @Override
    public GeneralResponse<PagingDTO<TourDiscountSummaryDTO>> getDiscounts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TourDiscount> discountPage = tourDiscountRepository
                .searchActiveDiscounts(keyword, pageable);
        PagingDTO<TourDiscountSummaryDTO> pagingDTO = PagingDTO.<TourDiscountSummaryDTO>builder()
                .page(discountPage.getNumber())
                .size(discountPage.getSize())
                .total(discountPage.getTotalElements())
                .items(discountPage.getContent().stream().map(tourDiscountMapper::toSummaryDTO).toList())
                .build();
        return GeneralResponse.of(pagingDTO, Constants.Message.TOUR_DISCOUNT_LIST_SUCCESS);
    }

    @Override
    public GeneralResponse<PagingDTO<TourResponseManagerDTO>> getToursForDiscount(String keyword, int page, int size, Boolean hasDiscount) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        String keywordPattern = (keyword == null || keyword.isBlank()) ? null : "%" + keyword.toLowerCase() + "%";
        Page<com.fpt.capstone.tourism.model.tour.Tour> tourPage =
                tourRepository.findToursForDiscount(keywordPattern, hasDiscount, pageable);
        PagingDTO<TourResponseManagerDTO> pagingDTO = PagingDTO.<TourResponseManagerDTO>builder()
                .page(tourPage.getNumber())
                .size(tourPage.getSize())
                .total(tourPage.getTotalElements())
                .items(tourPage.getContent().stream().map(tourManagementMapper::toTourResponseDTO).toList())
                .build();
        return GeneralResponse.of(pagingDTO);
    }

    /**
     * A helper method to centralize validation logic for creating and updating discounts.
     * @param requestDTO The DTO with discount data.
     * @param schedule The associated tour schedule.
     * @param currentDiscountId The ID of the discount being updated, or null if creating a new one.
     */
    private void validateDiscountRequest(TourDiscountRequestDTO requestDTO, TourSchedule schedule, Long currentDiscountId) {
        // 1. Validate discount percentage
        if (requestDTO.getDiscountPercent() <= 0 || requestDTO.getDiscountPercent() > 100) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DISCOUNT_INVALID_PERCENT);
        }
        // 2. Validate date range correctness
        if (!requestDTO.getStartDate().isBefore(requestDTO.getEndDate())) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DISCOUNT_INVALID_DATE_RANGE);
        }
        // 3.  Validate that the discount ends before or on the departure date
        if (requestDTO.getEndDate().isAfter(schedule.getDepartureDate())) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DISCOUNT_END_DATE_AFTER_DEPARTURE);
        }
        // 4. Check for overlapping discounts on the same schedule
        tourDiscountRepository.findByTourSchedule_IdAndDeletedFalse(schedule.getId())
                .ifPresent(existing -> {
                    if (currentDiscountId == null || !existing.getId().equals(currentDiscountId)) {
                        throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DISCOUNT_EXISTS);
                    }
                });
        tourDiscountRepository
                .findOverlappingDiscounts(
                        schedule.getId(),
                        requestDTO.getStartDate(),
                        requestDTO.getEndDate(),
                        currentDiscountId // Pass the current ID to exclude it from the check
                )
                .ifPresent(d -> {
                    throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DISCOUNT_EXISTS);
                });
    }
}