package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ServiceCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.ServiceDetailDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.partner.ServiceInfoMapper;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;
import com.fpt.capstone.tourism.model.partner.Partner;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import com.fpt.capstone.tourism.repository.partner.PartnerRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.repository.partner.ServiceTypeRepository;
import com.fpt.capstone.tourism.service.ServiceManagementService;
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

import static com.fpt.capstone.tourism.constants.Constants.Message.*;

@Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl implements ServiceManagementService {

    private final PartnerServiceRepository partnerServiceRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final PartnerRepository partnerRepository;
    private final ServiceInfoMapper serviceInfoMapper;

    @Override
    public GeneralResponse<PagingDTO<ServiceInfoDTO>> getServices(int page, int size, String keyword) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            Page<PartnerService> servicePage;
            if (StringUtils.hasText(keyword)) {
                servicePage = partnerServiceRepository
                        .findByStatusNotAndNameContainingIgnoreCase(PartnerServiceStatus.PENDING, keyword, pageable);
            } else {
                servicePage = partnerServiceRepository.findByStatusNot(PartnerServiceStatus.PENDING, pageable);
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
            return new GeneralResponse<>(HttpStatus.OK.value(), GET_SERVICE_LIST_SUCCESS, pagingDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GET_SERVICE_LIST_FAIL, ex);
        }
    }
    @Override
    public GeneralResponse<ServiceInfoDTO> createService(ServiceCreateRequestDTO dto) {
        try {
            ServiceType type = serviceTypeRepository.findById(dto.getServiceTypeId())
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_TYPE_NOT_FOUND));

            if (dto.getPartnerId() == null) {
                throw BusinessException.of(HttpStatus.BAD_REQUEST, SERVICE_PROVIDER_NOT_FOUND);
            }
            Partner partner = partnerRepository.findById(Math.toIntExact(dto.getPartnerId()))
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_PROVIDER_NOT_FOUND));

            PartnerService service = PartnerService.builder()
                    .name(dto.getName())
                    .imageUrl(dto.getImageUrl())
                    .description(dto.getDescription())
                    .nettPrice(dto.getNettPrice())
                    .sellingPrice(dto.getSellingPrice())
                    .costType(dto.getCostType())
                    .serviceType(type)
                    .partner(partner)
                    .status(PartnerServiceStatus.ACTIVE)
                    .build();

            PartnerService saved = partnerServiceRepository.save(service);
            return new GeneralResponse<>(HttpStatus.CREATED.value(), SERVICE_CREATED, serviceInfoMapper.toDto(saved));
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(SERVICE_CREATE_FAIL, ex);
        }
    }


    @Override
    public GeneralResponse<String> deleteService(Long id) {
        try {
            PartnerService service = partnerServiceRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_NOT_FOUND));
            service.softDelete();
            partnerServiceRepository.save(service);
            return new GeneralResponse<>(HttpStatus.OK.value(), SERVICE_DELETE_SUCCESS, null);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(SERVICE_DELETE_FAIL, ex);
        }
    }
    @Override
    public GeneralResponse<ServiceDetailDTO> getServiceDetail(Long id) {
        try {
            PartnerService service = partnerServiceRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_NOT_FOUND));
            return new GeneralResponse<>(HttpStatus.OK.value(), GET_SERVICE_DETAIL_SUCCESS, serviceInfoMapper.toDetailDto(service));
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GET_SERVICE_DETAIL_FAIL, ex);
        }
    }
    @Override
    public GeneralResponse<ServiceInfoDTO> changeServiceStatus(Long id, PartnerServiceStatus status) {
        try {
            PartnerService service = partnerServiceRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_NOT_FOUND));
            service.setStatus(status);
            PartnerService saved = partnerServiceRepository.save(service);
            return new GeneralResponse<>(HttpStatus.OK.value(), SERVICE_STATUS_UPDATED, serviceInfoMapper.toDto(saved));
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(SERVICE_STATUS_UPDATE_FAIL, ex);
        }
    }

}