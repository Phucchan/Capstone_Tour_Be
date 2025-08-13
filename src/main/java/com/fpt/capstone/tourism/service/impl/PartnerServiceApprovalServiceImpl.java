package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.PendingServiceUpdateDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.mapper.partner.ServiceInfoMapper;
import com.fpt.capstone.tourism.model.enums.CostType;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;
import com.fpt.capstone.tourism.model.partner.Partner;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.service.PartnerServiceApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerServiceApprovalServiceImpl implements PartnerServiceApprovalService {

    private final PartnerServiceRepository partnerServiceRepository;
    private final ServiceInfoMapper serviceInfoMapper;

    @Override
    public GeneralResponse<PagingDTO<ServiceInfoDTO>> getPendingServices(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<PartnerService> servicePage;
        if (StringUtils.hasText(keyword)) {
            servicePage = partnerServiceRepository
                    .findByStatusAndNameContainingIgnoreCase(PartnerServiceStatus.PENDING, keyword, pageable);
        } else {
            servicePage = partnerServiceRepository.findByStatus(PartnerServiceStatus.PENDING, pageable);
        }
        List<ServiceInfoDTO> dtos = servicePage.getContent().stream()
                .map(serviceInfoMapper::toDto)
                .collect(Collectors.toList());
        PagingDTO<ServiceInfoDTO> pagingDTO = PagingDTO.<ServiceInfoDTO>builder()
                .page(servicePage.getNumber())
                .size(servicePage.getSize())
                .total(servicePage.getTotalElements())
                .items(dtos)
                .build();
        return GeneralResponse.of(pagingDTO);
    }


    @Override
    public GeneralResponse<ServiceInfoDTO> updateService(Long id, PendingServiceUpdateDTO dto) {
        PartnerService service = partnerServiceRepository.findById(id).orElse(null);
        if (service == null) {
            return new GeneralResponse<>(HttpStatus.NOT_FOUND.value(), "Service not found", null);
        }
        if (dto.getPartnerId() != null) {
            service.setPartner(Partner.builder().id(dto.getPartnerId()).build());
        }
        if (dto.getImageUrl() != null) {
            service.setImageUrl(dto.getImageUrl());
        }
        if (dto.getDescription() != null) {
            service.setDescription(dto.getDescription());
        }
        if (dto.getNettPrice() != null) {
            service.setNettPrice(dto.getNettPrice());
        }
        if (dto.getSellingPrice() != null) {
            service.setSellingPrice(dto.getSellingPrice());
        }
        if (dto.getCostType() != null) {
            service.setCostType(CostType.valueOf(dto.getCostType()));
        }
        if (dto.getNewStatus() != null) {
            service.setStatus(PartnerServiceStatus.valueOf(dto.getNewStatus()));
        }
        PartnerService saved = partnerServiceRepository.save(service);
        return GeneralResponse.of(serviceInfoMapper.toDto(saved));
    }
}