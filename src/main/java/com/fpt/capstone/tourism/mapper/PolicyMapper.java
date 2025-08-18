package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.response.PolicyDTO;
import com.fpt.capstone.tourism.model.Policy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PolicyMapper {
    PolicyDTO policyToPolicyDTO(Policy policy);
}
