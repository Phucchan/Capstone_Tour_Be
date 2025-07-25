package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.VoucherDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.VoucherRequestDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;

public interface VoucherService {
    GeneralResponse<VoucherDTO> createVoucher(VoucherRequestDTO requestDTO);

    GeneralResponse<PagingDTO<VoucherSummaryDTO>> getVouchers(String keyword, int page, int size);
}