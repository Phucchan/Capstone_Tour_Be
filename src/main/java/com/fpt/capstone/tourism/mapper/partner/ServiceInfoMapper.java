package com.fpt.capstone.tourism.mapper.partner;

import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ServiceInfoMapper {

    @Mapping(source = "id", target = "id")
    // Lấy tên của dịch vụ từ chính entity PartnerService
    @Mapping(source = "name", target = "name")
    // Lấy tên đối tác từ PartnerService -> Partner -> name
    @Mapping(source = "partner.name", target = "partnerName")
    // Lấy tên loại dịch vụ từ PartnerService -> ServiceType -> name
    @Mapping(source = "serviceType.name", target = "serviceTypeName")
    ServiceInfoDTO toDto(PartnerService partnerService);
}
