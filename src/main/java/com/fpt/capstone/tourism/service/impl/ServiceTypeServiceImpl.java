package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.common.ServiceTypeDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
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
}