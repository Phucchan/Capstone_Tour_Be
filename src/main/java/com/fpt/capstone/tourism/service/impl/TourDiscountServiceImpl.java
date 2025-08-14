package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.TourDiscountDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.TourDiscountRequestDTO;
import com.fpt.capstone.tourism.dto.response.TourDiscountSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourDiscountMapper;
import com.fpt.capstone.tourism.model.tour.TourDiscount;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TourDiscountServiceImpl implements TourDiscountService {

    private final TourDiscountRepository tourDiscountRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final TourDiscountMapper tourDiscountMapper;

    @Override
    public GeneralResponse<TourDiscountDTO> createDiscount(TourDiscountRequestDTO requestDTO) {
        try {
            TourSchedule schedule = tourScheduleRepository.findById(requestDTO.getScheduleId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, Constants.Message.TOUR_SCHEDULE_NOT_FOUND));

            tourDiscountRepository
                    .findFirstByTourSchedule_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
                            schedule.getId(), requestDTO.getStartDate(), requestDTO.getEndDate())
                    .ifPresent(d -> {
                        throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.TOUR_DISCOUNT_EXISTS);
                    });

            TourDiscount discount = TourDiscount.builder()
                    .tourSchedule(schedule)
                    .discountPercent(requestDTO.getDiscountPercent())
                    .startDate(requestDTO.getStartDate())
                    .endDate(requestDTO.getEndDate())
                    .build();
            discount.setDeleted(false);
            TourDiscount saved = tourDiscountRepository.save(discount);
            TourDiscountDTO dto = tourDiscountMapper.toDTO(saved);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.TOUR_DISCOUNT_CREATE_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.TOUR_DISCOUNT_CREATE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<PagingDTO<TourDiscountSummaryDTO>> getDiscounts(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "discountPercent"));
            Page<TourDiscount> discountPage = tourDiscountRepository
                    .searchActiveDiscounts(keyword, LocalDateTime.now(), pageable);
            PagingDTO<TourDiscountSummaryDTO> pagingDTO = PagingDTO.<TourDiscountSummaryDTO>builder()
                    .page(discountPage.getNumber())
                    .size(discountPage.getSize())
                    .total(discountPage.getTotalElements())
                    .items(discountPage.getContent().stream().map(tourDiscountMapper::toSummaryDTO).toList())
                    .build();
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.TOUR_DISCOUNT_LIST_SUCCESS, pagingDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.TOUR_DISCOUNT_LIST_FAIL, ex);
        }
    }
}