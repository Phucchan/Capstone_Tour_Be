package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.PartnerServiceShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.service.PartnerServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnerServiceServiceImpl implements PartnerServiceService {

    private final PartnerServiceRepository partnerServiceRepository;

    @Override
    public GeneralResponse<List<PartnerServiceShortDTO>> getPartnerServices() {
        try {
            List<PartnerService> services = partnerServiceRepository.findByDeletedFalse();
            List<PartnerServiceShortDTO> dtos = services.stream()
                    .map(s -> PartnerServiceShortDTO.builder()
                            .id(s.getId())
                            .name(s.getDescription())
                            .partnerName(s.getPartner().getName())
                            .serviceTypeName(s.getServiceType().getName())
                            .build())
                    .collect(Collectors.toList());
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.GET_SERVICE_LIST_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.GET_SERVICE_LIST_FAIL, ex);
        }
    }
}
