package com.fpt.capstone.tourism.mapper.tourManager;

import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourDayManagerDTO;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import com.fpt.capstone.tourism.model.tour.TourDay;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TourDayManagerMapper {

    @Mapping(target = "services", source = "services")
    @Mapping(target = "serviceTypes", source = "serviceTypes")
    TourDayManagerDTO toDTO(TourDay day);

    @Mapping(target = "partnerName", source = "partner.name")
    @Mapping(target = "serviceTypeName", source = "serviceType.name")
    ServiceInfoDTO toServiceDTO(PartnerService service);
    ServiceTypeShortDTO toServiceTypeShortDTO(ServiceType serviceType);
}