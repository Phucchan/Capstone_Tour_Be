package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.tour.FeedbackDTO;
import com.fpt.capstone.tourism.dto.response.tour.ServiceSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDayDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.tour.Feedback;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.tour.TourDay;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourDetailMapper {

    @Mapping(source = "tourTheme.name", target = "tourThemeName")
    TourDetailDTO tourToTourDetailDTO(Tour tour);

    @Mapping(source = "location.name", target = "locationName")
    TourDayDetailDTO tourDayToTourDayDetailDTO(TourDay tourDay);

    @Mapping(source = "user.fullName", target = "userName")
    @Mapping(source = "user.avatarImage", target = "userAvatarUrl")
    FeedbackDTO feedbackToFeedbackDTO(Feedback feedback);

    @Mapping(source = "tourPax.sellingPrice", target = "price")
        // Giả sử cần tính số ghế còn lại, nếu không có thì có thể bỏ qua
        // @Mapping(target = "availableSeats", expression = "java(...)")
    TourScheduleDTO tourScheduleToTourScheduleDTO(TourSchedule tourSchedule);

    @Mapping(source = "partner.name", target = "name")
    @Mapping(source = "serviceType.name", target = "type")
    ServiceSummaryDTO partnerServiceToServiceSummaryDTO(PartnerService partnerService);
}
