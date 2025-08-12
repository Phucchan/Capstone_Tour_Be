package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.mapper.partner.ServiceInfoMapper;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.service.PartnerServiceApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerServiceApprovalServiceImpl implements PartnerServiceApprovalService {

    private final PartnerServiceRepository partnerServiceRepository;
    private final ServiceInfoMapper serviceInfoMapper;

    @Override
    public GeneralResponse<List<ServiceInfoDTO>> getPendingServices() {
        List<PartnerService> services = partnerServiceRepository.findByStatus(PartnerServiceStatus.PENDING);
        List<ServiceInfoDTO> dtos = services.stream()
                .map(serviceInfoMapper::toDto)
                .collect(Collectors.toList());
        return GeneralResponse.of(dtos);
    }

    @Override
    public GeneralResponse<ServiceInfoDTO> changeStatus(Long id, ChangeStatusDTO dto) {
        PartnerService service = partnerServiceRepository.findById(id).orElse(null);
        if (service == null) {
            return new GeneralResponse<>(HttpStatus.NOT_FOUND.value(), "Service not found", null);
        }
        PartnerServiceStatus status = PartnerServiceStatus.valueOf(dto.getNewStatus());
        service.setStatus(status);
        PartnerService saved = partnerServiceRepository.save(service);
        return GeneralResponse.of(serviceInfoMapper.toDto(saved));
    }
}