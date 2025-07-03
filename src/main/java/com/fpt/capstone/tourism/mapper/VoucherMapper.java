package com.fpt.capstone.tourism.mapper;

import com.fpt.capstone.tourism.dto.common.VoucherDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;
import com.fpt.capstone.tourism.model.voucher.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VoucherMapper {
    @Mapping(source = "createdBy.fullName", target = "createdByName")
    VoucherDTO toDTO(Voucher voucher);

    VoucherSummaryDTO toSummaryDTO(Voucher voucher);
}