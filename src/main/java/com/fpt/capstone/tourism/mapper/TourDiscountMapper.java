package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.common.TourDiscountDTO;
import com.fpt.capstone.tourism.dto.response.TourDiscountSummaryDTO;
import com.fpt.capstone.tourism.model.tour.TourDiscount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourDiscountMapper {
    @Mapping(source = "tourSchedule.id", target = "scheduleId")
    @Mapping(source = "tourSchedule.tour.id", target = "tourId")
    @Mapping(source = "tourSchedule.tour.name", target = "tourName")
    TourDiscountDTO toDTO(TourDiscount discount);

    @Mapping(source = "tourSchedule.id", target = "scheduleId")
    @Mapping(source = "tourSchedule.tour.name", target = "tourName")
    TourDiscountSummaryDTO toSummaryDTO(TourDiscount discount);
}