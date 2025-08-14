package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.VoucherDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.VoucherRequestDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.VoucherMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.VoucherStatus;
import com.fpt.capstone.tourism.model.voucher.Voucher;
import com.fpt.capstone.tourism.repository.voucher.VoucherRepository;
import com.fpt.capstone.tourism.service.UserService;
import com.fpt.capstone.tourism.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserService userService;
    private final VoucherMapper voucherMapper;

    @Override
    public GeneralResponse<VoucherDTO> createVoucher(VoucherRequestDTO requestDTO) {
        try {
            if (voucherRepository.findByCode(requestDTO.getCode()).isPresent()) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, Constants.Message.VOUCHER_CODE_EXISTS);
            }
            User creator = userService.findById(requestDTO.getCreatedBy());
            Voucher voucher = Voucher.builder()
                    .code(requestDTO.getCode())
                    .description(requestDTO.getDescription())
                    .discountAmount(requestDTO.getDiscountAmount())
                    .pointsRequired(requestDTO.getPointsRequired())
                    .minOrderValue(requestDTO.getMinOrderValue())
                    .validFrom(requestDTO.getValidFrom())
                    .validTo(requestDTO.getValidTo())
                    .maxUsage(requestDTO.getMaxUsage())
                    .voucherStatus(VoucherStatus.ACTIVE)
                    .createdBy(creator)
                    .build();
            voucher.setDeleted(false);
            Voucher saved = voucherRepository.save(voucher);
            VoucherDTO dto = voucherMapper.toDTO(saved);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.VOUCHER_CREATE_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.VOUCHER_CREATE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<PagingDTO<VoucherSummaryDTO>> getVouchers(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Voucher> voucherPage;
            if (keyword != null && !keyword.isBlank()) {
                voucherPage = voucherRepository
                        .findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
            } else {
                voucherPage = voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
            }
            PagingDTO<VoucherSummaryDTO> pagingDTO = PagingDTO.<VoucherSummaryDTO>builder()
                    .page(voucherPage.getNumber())
                    .size(voucherPage.getSize())
                    .total(voucherPage.getTotalElements())
                    .items(voucherPage.getContent().stream().map(voucherMapper::toSummaryDTO).toList())
                    .build();
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.VOUCHER_LIST_SUCCESS, pagingDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.VOUCHER_LIST_FAIL, ex);
        }
    }


}