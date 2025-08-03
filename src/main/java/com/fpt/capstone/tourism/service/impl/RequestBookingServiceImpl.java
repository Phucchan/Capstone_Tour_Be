package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.dto.response.RequestBookingNotificationDTO;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.RequestBookingRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.RequestBookingService;
import com.fpt.capstone.tourism.mapper.booking.RequestBookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestBookingServiceImpl implements RequestBookingService {

    private final RequestBookingRepository requestBookingRepository;
    private final RequestBookingMapper requestBookingMapper;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;


    @Override
    public GeneralResponse<RequestBookingDTO> createRequest(RequestBookingDTO requestBookingDTO) {
        RequestBooking requestBooking = requestBookingMapper.toEntity(requestBookingDTO);
        if (requestBookingDTO.getUserId() != null) {
            requestBooking.setUser(userRepository.findUserById(requestBookingDTO.getUserId()).orElse(null));
        }
        if (requestBookingDTO.getDepartureLocationId() != null) {
            requestBooking.setDepartureLocation(locationRepository.findById(requestBookingDTO.getDepartureLocationId()).orElse(null));
        }
        RequestBooking saved = requestBookingRepository.save(requestBooking);
        notifyNewRequestBooking(saved);
        RequestBookingDTO savedDto = requestBookingMapper.toDTO(saved);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Request saved", savedDto);
    }
    @Override
    public GeneralResponse<RequestBookingDTO> getRequest(Long id) {
        RequestBooking booking = requestBookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return new GeneralResponse<>(HttpStatus.NOT_FOUND.value(), "Request not found", null);
        }
        RequestBookingDTO dto = requestBookingMapper.toDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), dto);
    }

    private void notifyNewRequestBooking(RequestBooking requestBooking) {
        RequestBookingNotificationDTO dto = RequestBookingNotificationDTO.builder()
                .id(requestBooking.getId())
                .customerName(requestBooking.getCustomerName())
                .customerPhone(requestBooking.getCustomerPhone())
                .customerEmail(requestBooking.getCustomerEmail())
                .destination(requestBooking.getDestination())
                .startDate(requestBooking.getStartDate())
                .endDate(requestBooking.getEndDate())
                .createdAt(requestBooking.getCreatedAt())
                .detailUrl(frontendBaseUrl + "/request-bookings/" + requestBooking.getId())
                .build();
        messagingTemplate.convertAndSend("/topic/request-bookings", dto);
    }
}