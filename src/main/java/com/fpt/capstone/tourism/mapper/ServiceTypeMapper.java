package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.common.ServiceTypeDTO;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceTypeMapper extends EntityMapper<ServiceTypeDTO, ServiceType> {
    @Override
    ServiceTypeDTO toDTO(ServiceType entity);

    @Override
    ServiceType toEntity(ServiceTypeDTO dto);
}