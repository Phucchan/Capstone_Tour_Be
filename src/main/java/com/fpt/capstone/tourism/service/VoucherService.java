package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.common.VoucherDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.VoucherRequestDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.UserVoucherSummaryDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;

public interface VoucherService {
    GeneralResponse<VoucherDTO> createVoucher(VoucherRequestDTO requestDTO);

    GeneralResponse<PagingDTO<VoucherSummaryDTO>> getVouchers(String keyword, int page, int size);

    GeneralResponse<PagingDTO<VoucherSummaryDTO>> getAvailableVouchers(String keyword, int page, int size);

    GeneralResponse<String> redeemVoucher(Long userId, Long voucherId);

    GeneralResponse<java.util.List<UserVoucherSummaryDTO>> getUserVouchers(Long userId);
}