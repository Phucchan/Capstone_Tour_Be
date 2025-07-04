package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.repository.RequestBookingRepository;
import com.fpt.capstone.tourism.service.RequestBookingService;
import com.fpt.capstone.tourism.mapper.RequestBookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestBookingServiceImpl implements RequestBookingService {

    private final RequestBookingRepository requestBookingRepository;
    private final RequestBookingMapper requestBookingMapper;

    @Override
    public GeneralResponse<RequestBookingDTO> createRequest(RequestBookingDTO requestBookingDTO) {
        RequestBooking requestBooking = requestBookingMapper.toEntity(requestBookingDTO);
        RequestBooking saved = requestBookingRepository.save(requestBooking);
        RequestBookingDTO savedDto = requestBookingMapper.toDTO(saved);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Request saved", savedDto);
    }
}