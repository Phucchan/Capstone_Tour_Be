package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.VoucherDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.VoucherRequestDTO;
import com.fpt.capstone.tourism.dto.response.UserVoucherSummaryDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.VoucherMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.VoucherStatus;
import com.fpt.capstone.tourism.model.voucher.UserVoucher;
import com.fpt.capstone.tourism.model.voucher.Voucher;
import com.fpt.capstone.tourism.repository.user.UserVoucherRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final UserVoucherRepository userVoucherRepository;
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
    @Override
    public GeneralResponse<PagingDTO<VoucherSummaryDTO>> getAvailableVouchers(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            LocalDateTime now = LocalDateTime.now();
            String searchKeyword = (keyword != null && !keyword.isBlank()) ? "%" + keyword.toLowerCase() + "%" : null;
            Page<Voucher> voucherPage = voucherRepository.findAvailableVouchers(searchKeyword, VoucherStatus.ACTIVE, now, pageable);
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

    @Override
    @Transactional
    public GeneralResponse<String> redeemVoucher(Long userId, Long voucherId) {
        try {
            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.VOUCHER_NOT_FOUND));

            LocalDateTime now = LocalDateTime.now();
            if ((voucher.getDeleted() != null && voucher.getDeleted())
                    || voucher.getVoucherStatus() != VoucherStatus.ACTIVE
                    || (voucher.getValidFrom() != null && voucher.getValidFrom().isAfter(now))
                    || (voucher.getValidTo() != null && voucher.getValidTo().isBefore(now))) {
                throw BusinessException.of(Constants.Message.VOUCHER_REDEEM_FAIL);
            }

            if (voucher.getMaxUsage() != null && voucher.getMaxUsage() <= 0) {
                throw BusinessException.of(Constants.Message.VOUCHER_OUT_OF_STOCK);
            }

            User user = userService.findById(userId);
            int currentPoints = user.getPoints() != null ? user.getPoints() : 0;
            if (currentPoints < voucher.getPointsRequired()) {
                throw BusinessException.of(Constants.Message.VOUCHER_NOT_ENOUGH_POINTS);
            }

            user.setPoints(currentPoints - voucher.getPointsRequired());
            userService.saveUser(user);

            if (voucher.getMaxUsage() != null) {
                voucher.setMaxUsage(voucher.getMaxUsage() - 1);
                if (voucher.getMaxUsage() <= 0) {
                    voucher.setVoucherStatus(VoucherStatus.USED);
                }
            }
            voucherRepository.save(voucher);
            UserVoucher userVoucher = UserVoucher.builder()
                    .user(user)
                    .voucher(voucher)
                    .redeemedAt(now)
                    .quantity(1)
                    .build();
            userVoucher.setDeleted(false);
            userVoucherRepository.save(userVoucher);

            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.VOUCHER_REDEEM_SUCCESS, voucher.getCode());
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.VOUCHER_REDEEM_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<List<UserVoucherSummaryDTO>> getUserVouchers(Long userId) {
        try {
            List<UserVoucher> userVouchers = userVoucherRepository.findByUserId(userId);
            LocalDateTime now = LocalDateTime.now();
            List<UserVoucherSummaryDTO> dtos = userVouchers.stream()
                    .filter(uv -> uv.getQuantity() != null && uv.getQuantity() > 0)
                    .filter(uv -> {
                        Voucher v = uv.getVoucher();
                        return v.getVoucherStatus() == VoucherStatus.ACTIVE
                                && (v.getValidFrom() == null || !v.getValidFrom().isAfter(now))
                                && (v.getValidTo() == null || !v.getValidTo().isBefore(now))
                                && (v.getMaxUsage() == null || v.getMaxUsage() > 0);
                    })
                    .map(uv -> UserVoucherSummaryDTO.builder()
                            .id(uv.getId())
                            .voucherId(uv.getVoucher().getId())
                            .code(uv.getVoucher().getCode())
                            .discountAmount(uv.getVoucher().getDiscountAmount())
                            .quantity(uv.getQuantity() != null ? uv.getQuantity() : 0)
                            .build())
                    .collect(Collectors.toList());
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.VOUCHER_LIST_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.VOUCHER_LIST_FAIL, ex);
        }
    }

}