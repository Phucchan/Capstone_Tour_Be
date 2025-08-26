package com.fpt.capstone.tourism.mapper.partner;

import com.fpt.capstone.tourism.dto.response.ServiceDetailDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceInfoMapper {

    @Mapping(source = "id", target = "id")
    // Lấy tên của dịch vụ từ chính entity PartnerService
    @Mapping(source = "name", target = "name")
    // Lấy tên đối tác từ PartnerService -> Partner -> name
    @Mapping(source = "partner.name", target = "partnerName")
    // Lấy tên loại dịch vụ từ PartnerService -> ServiceType -> name
    @Mapping(source = "serviceType.name", target = "serviceTypeName")
    @Mapping(source = "status", target = "status")
    ServiceInfoDTO toDto(PartnerService partnerService);

    @Mapping(source = "partner.id", target = "partnerId")
    @Mapping(source = "partner.name", target = "partnerName")
    @Mapping(source = "serviceType.id", target = "serviceTypeId")
    @Mapping(source = "serviceType.name", target = "serviceTypeName")
    @Mapping(source = "status", target = "status")
    ServiceDetailDTO toDetailDto(PartnerService partnerService);
}
