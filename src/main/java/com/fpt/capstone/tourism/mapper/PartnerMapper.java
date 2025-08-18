package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.PartnerSummaryDTO;
import com.fpt.capstone.tourism.model.partner.Partner;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PartnerMapper {
    PartnerSummaryDTO toSummaryDTO(Partner partner);
}