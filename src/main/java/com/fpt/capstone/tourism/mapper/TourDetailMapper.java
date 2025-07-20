package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.common.tour.TourScheduleShortInfoDTO;
import com.fpt.capstone.tourism.dto.common.tour.TourShortInfoDTO;
import com.fpt.capstone.tourism.dto.response.tour.FeedbackDTO;
import com.fpt.capstone.tourism.dto.response.tour.ServiceSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDayDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.tour.Feedback;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component("TourDetailMapper")
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourDetailMapper {

    @Mapping(source = "themes", target = "tourThemeName", qualifiedByName = "themesToString")

    @Mapping(source = "code", target = "code")

    @Mapping(source = "tourTransport", target = "tourTransport", qualifiedByName = "transportToString")

    @Mapping(source = "departLocation.name", target = "departLocationName")
    TourDetailDTO tourToTourDetailDTO(Tour tour);

    @Mapping(source = "themes", target = "tourThemeName", qualifiedByName = "themesToString")
    @Mapping(source = "code", target = "code")
    @Mapping(source = "departLocation.name", target = "departLocationName")
    @Mapping(source = "tourTransport", target = "tourTransport", qualifiedByName = "transportToString")
    TourShortInfoDTO toTourShortInfoDTO(Tour tour);


    @Mapping(source = "location.name", target = "locationName")
    TourDayDetailDTO tourDayToTourDayDetailDTO(TourDay tourDay);

    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "user.avatarImage", target = "userAvatarUrl")
    FeedbackDTO feedbackToFeedbackDTO(Feedback feedback);

    @Mapping(source = "tourPax.sellingPrice", target = "price")
    @Mapping(source = "tourPax.extraHotelCost", target = "extraHotelCost")
        // Giả sử cần tính số ghế còn lại, nếu không có thì có thể bỏ qua
        // @Mapping(target = "availableSeats", expression = "java(...)")
    TourScheduleDTO tourScheduleToTourScheduleDTO(TourSchedule tourSchedule);

    TourScheduleShortInfoDTO toTourScheduleShortInfoDTO(TourSchedule tourSchedule);

    @Mapping(source = "partner.name", target = "name")
    @Mapping(source = "serviceType.name", target = "type")
    ServiceSummaryDTO partnerServiceToServiceSummaryDTO(PartnerService partnerService);

    @Named("transportToString")
    default String transportToString(TourTransport transport) {
        if (transport == null) {
            return null;
        }
        return transport.name();
    }

    @Named("themesToString")
    default String themesToString(java.util.List<com.fpt.capstone.tourism.model.tour.TourTheme> themes) {
        if (themes == null || themes.isEmpty()) {
            return null;
        }
        return themes.stream().map(com.fpt.capstone.tourism.model.tour.TourTheme::getName)
                .collect(java.util.stream.Collectors.joining(", "));
    }
}
