package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.common.ServiceTypeDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.ChangeDeleteStatusDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.ServiceTypeMapper;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import com.fpt.capstone.tourism.repository.partner.ServiceTypeRepository;
import com.fpt.capstone.tourism.service.ServiceTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.fpt.capstone.tourism.constants.Constants.Message.GET_SERVICE_LIST_FAIL;
import static com.fpt.capstone.tourism.constants.Constants.Message.GET_SERVICE_LIST_SUCCESS;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_CREATE_FAIL;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_CREATE_SUCCESS;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_DELETE_FAIL;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_DELETE_SUCCESS;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_NOT_FOUND;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_STATUS_UPDATED;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_UPDATE_FAIL;
import static com.fpt.capstone.tourism.constants.Constants.Message.SERVICE_TYPE_UPDATE_SUCCESS;

@Service
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeMapper serviceTypeMapper;

    @Override
    public GeneralResponse<List<ServiceTypeDTO>> getServiceTypes() {
        try {
            List<ServiceType> entities = serviceTypeRepository.findAll();
            List<ServiceTypeDTO> dtos = entities.stream()
                    .map(serviceTypeMapper::toDTO)
                    .collect(Collectors.toList());
            return new GeneralResponse<>(HttpStatus.OK.value(), GET_SERVICE_LIST_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(GET_SERVICE_LIST_FAIL, ex);
        }
    }
    @Override
    public GeneralResponse<ServiceTypeDTO> createServiceType(ServiceTypeDTO dto) {
        try {
            ServiceType entity = serviceTypeMapper.toEntity(dto);
            entity.setDeleted(false);
            ServiceType saved = serviceTypeRepository.save(entity);
            return new GeneralResponse<>(HttpStatus.OK.value(), SERVICE_TYPE_CREATE_SUCCESS, serviceTypeMapper.toDTO(saved));
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(SERVICE_TYPE_CREATE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<ServiceTypeDTO> updateServiceType(Long id, ServiceTypeDTO dto) {
        try {
            ServiceType entity = serviceTypeRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_TYPE_NOT_FOUND));
            if (dto.getCode() != null) entity.setCode(dto.getCode());
            if (dto.getName() != null) entity.setName(dto.getName());
            ServiceType saved = serviceTypeRepository.save(entity);
            return new GeneralResponse<>(HttpStatus.OK.value(), SERVICE_TYPE_UPDATE_SUCCESS, serviceTypeMapper.toDTO(saved));
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(SERVICE_TYPE_UPDATE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<String> deleteServiceType(Long id) {
        try {
            ServiceType entity = serviceTypeRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_TYPE_NOT_FOUND));
            entity.softDelete();
            serviceTypeRepository.save(entity);
            return new GeneralResponse<>(HttpStatus.OK.value(), SERVICE_TYPE_DELETE_SUCCESS, null);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(SERVICE_TYPE_DELETE_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<ServiceTypeDTO> changeStatus(Long id, ChangeDeleteStatusDTO changeStatusDTO) {
        try {
            ServiceType entity = serviceTypeRepository.findById(id)
                    .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, SERVICE_TYPE_NOT_FOUND));
            if (Boolean.TRUE.equals(changeStatusDTO.getDeleted())) {
                entity.softDelete();
            } else {
                entity.restore();
            }
            ServiceType saved = serviceTypeRepository.save(entity);
            return new GeneralResponse<>(HttpStatus.OK.value(), SERVICE_TYPE_STATUS_UPDATED, serviceTypeMapper.toDTO(saved));
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(SERVICE_TYPE_UPDATE_FAIL, ex);
        }
    }
}