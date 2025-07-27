package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.PartnerSummaryDTO;
import com.fpt.capstone.tourism.dto.response.PartnerDetailDTO;
import com.fpt.capstone.tourism.dto.request.PartnerUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.PartnerMapper;
import com.fpt.capstone.tourism.model.partner.Partner;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.partner.ServiceTypeRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerRepository;
import com.fpt.capstone.tourism.service.PartnerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerManagementServiceImpl implements PartnerManagementService {

    private final PartnerRepository partnerRepository;
    private final PartnerMapper partnerMapper;
    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;
    private final ServiceTypeRepository serviceTypeRepository;

    @Override
    public GeneralResponse<PagingDTO<PartnerSummaryDTO>> getPartners(int page,
                                                                     int size,
                                                                     String keyword,
                                                                     Boolean isDeleted,
                                                                     String sortField,
                                                                     String sortDirection) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
            Pageable pageable = PageRequest.of(page, size, sort);
            Specification<Partner> specification = buildSpecification(keyword, isDeleted);
            Page<Partner> partnerPage = partnerRepository.findAll(specification, pageable);
            List<PartnerSummaryDTO> dtos = partnerPage.getContent().stream()
                    .map(partnerMapper::toSummaryDTO)
                    .toList();
            PagingDTO<PartnerSummaryDTO> pagingDTO = PagingDTO.<PartnerSummaryDTO>builder()
                    .page(partnerPage.getNumber())
                    .size(partnerPage.getSize())
                    .total(partnerPage.getTotalElements())
                    .items(dtos)
                    .build();
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.PARTNER_LIST_SUCCESS, pagingDTO);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.PARTNER_LIST_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<PartnerDetailDTO> getPartnerDetail(Long id) {
        try {
            Partner partner = partnerRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> BusinessException.of(Constants.Message.SERVICE_PROVIDER_NOT_FOUND));

            LocationShortDTO location = locationMapper.toLocationShortDTO(partner.getLocation());
            ServiceTypeShortDTO serviceType = ServiceTypeShortDTO.builder()
                    .id(partner.getServiceType().getId())
                    .code(partner.getServiceType().getCode())
                    .name(partner.getServiceType().getName())
                    .build();

            List<LocationShortDTO> locationOptions = locationRepository.findAllLocations().stream()
                    .map(locationMapper::toLocationShortDTO)
                    .toList();

            List<ServiceTypeShortDTO> serviceTypeOptions = serviceTypeRepository.findAll().stream()
                    .map(st -> ServiceTypeShortDTO.builder()
                            .id(st.getId())
                            .code(st.getCode())
                            .name(st.getName())
                            .build())
                    .toList();

            PartnerDetailDTO dto = PartnerDetailDTO.builder()
                    .id(partner.getId())
                    .name(partner.getName())
                    .logoUrl(partner.getLogoUrl())
                    .contactPhone(partner.getContactPhone())
                    .contactEmail(partner.getContactEmail())
                    .contactName(partner.getContactName())
                    .description(partner.getDescription())
                    .websiteUrl(partner.getWebsiteUrl())
                    .location(location)
                    .serviceType(serviceType)
                    .locationOptions(locationOptions)
                    .serviceTypeOptions(serviceTypeOptions)
                    .build();

            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.PARTNER_DETAIL_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.PARTNER_DETAIL_FAIL, ex);
        }
    }
    @Override
    @Transactional
    public GeneralResponse<PartnerDetailDTO> updatePartner(Long id, PartnerUpdateRequestDTO requestDTO) {
        try {
            Partner partner = partnerRepository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> BusinessException.of(Constants.Message.SERVICE_PROVIDER_NOT_FOUND));

            if (requestDTO.getName() != null) partner.setName(requestDTO.getName());
            if (requestDTO.getLogoUrl() != null) partner.setLogoUrl(requestDTO.getLogoUrl());
            if (requestDTO.getContactPhone() != null) partner.setContactPhone(requestDTO.getContactPhone());
            if (requestDTO.getContactEmail() != null) partner.setContactEmail(requestDTO.getContactEmail());
            if (requestDTO.getContactName() != null) partner.setContactName(requestDTO.getContactName());
            if (requestDTO.getDescription() != null) partner.setDescription(requestDTO.getDescription());
            if (requestDTO.getWebsiteUrl() != null) partner.setWebsiteUrl(requestDTO.getWebsiteUrl());
            if (requestDTO.getLocationId() != null) {
                Location location = locationRepository.findById(requestDTO.getLocationId())
                        .orElseThrow(() -> BusinessException.of(Constants.Message.LOCATION_NOT_FOUND));
                partner.setLocation(location);
            }
            if (requestDTO.getServiceTypeId() != null) {
                ServiceType serviceType = serviceTypeRepository.findById(requestDTO.getServiceTypeId())
                        .orElseThrow(() -> BusinessException.of(Constants.Message.SERVICE_TYPE_NOT_FOUND));
                partner.setServiceType(serviceType);
            }

            Partner saved = partnerRepository.save(partner);

            PartnerDetailDTO dto = buildDetailDTO(saved);

            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.PARTNER_UPDATE_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.PARTNER_UPDATE_FAIL, ex);
        }
    }
    @Override
    @Transactional
    public GeneralResponse<PartnerDetailDTO> addPartner(PartnerUpdateRequestDTO requestDTO) {
        try {
            Partner partner = new Partner();
            partner.setName(requestDTO.getName());
            partner.setLogoUrl(requestDTO.getLogoUrl());
            partner.setContactPhone(requestDTO.getContactPhone());
            partner.setContactEmail(requestDTO.getContactEmail());
            partner.setContactName(requestDTO.getContactName());
            partner.setDescription(requestDTO.getDescription());
            partner.setWebsiteUrl(requestDTO.getWebsiteUrl());

            Location location = locationRepository.findById(requestDTO.getLocationId())
                    .orElseThrow(() -> BusinessException.of(Constants.Message.LOCATION_NOT_FOUND));
            partner.setLocation(location);

            ServiceType serviceType = serviceTypeRepository.findById(requestDTO.getServiceTypeId())
                    .orElseThrow(() -> BusinessException.of(Constants.Message.SERVICE_TYPE_NOT_FOUND));
            partner.setServiceType(serviceType);

            Partner saved = partnerRepository.save(partner);
            PartnerDetailDTO dto = buildDetailDTO(saved);

            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.PARTNER_ADD_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.PARTNER_ADD_FAIL, ex);
        }
    }

    private PartnerDetailDTO buildDetailDTO(Partner partner) {
        LocationShortDTO location = locationMapper.toLocationShortDTO(partner.getLocation());
        ServiceTypeShortDTO serviceType = ServiceTypeShortDTO.builder()
                .id(partner.getServiceType().getId())
                .code(partner.getServiceType().getCode())
                .name(partner.getServiceType().getName())
                .build();

        List<LocationShortDTO> locationOptions = locationRepository.findAllLocations().stream()
                .map(locationMapper::toLocationShortDTO)
                .toList();

        List<ServiceTypeShortDTO> serviceTypeOptions = serviceTypeRepository.findAll().stream()
                .map(st -> ServiceTypeShortDTO.builder()
                        .id(st.getId())
                        .code(st.getCode())
                        .name(st.getName())
                        .build())
                .toList();

        return PartnerDetailDTO.builder()
                .id(partner.getId())
                .name(partner.getName())
                .logoUrl(partner.getLogoUrl())
                .contactPhone(partner.getContactPhone())
                .contactEmail(partner.getContactEmail())
                .contactName(partner.getContactName())
                .description(partner.getDescription())
                .websiteUrl(partner.getWebsiteUrl())
                .location(location)
                .serviceType(serviceType)
                .locationOptions(locationOptions)
                .serviceTypeOptions(serviceTypeOptions)
                .build();
    }

    private Specification<Partner> buildSpecification(String keyword, Boolean isDeleted) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), likeKeyword));
            }
            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("deleted"), isDeleted));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}