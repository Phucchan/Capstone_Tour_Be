package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.dto.response.RequestBookingNotificationDTO;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.RequestBookingRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.RequestBookingService;
import com.fpt.capstone.tourism.mapper.booking.RequestBookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        if (requestBookingDTO.getUserId() == null) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "User account is required", null);
        }

        if (requestBookingDTO.getDepartureLocationId() == null ||
                requestBookingDTO.getDestinationLocationIds() == null ||
                requestBookingDTO.getDestinationLocationIds().isEmpty() ||
                requestBookingDTO.getTourTheme() == null || requestBookingDTO.getTourTheme().isBlank() ||
                requestBookingDTO.getDesiredDepartureDate() == null ||
                requestBookingDTO.getDesiredServices() == null || requestBookingDTO.getDesiredServices().isBlank() ||
                requestBookingDTO.getStartDate() == null ||
                requestBookingDTO.getEndDate() == null ||
                requestBookingDTO.getTransport() == null ||
                requestBookingDTO.getAdults() == null ||
                requestBookingDTO.getChildren() == null ||
                requestBookingDTO.getInfants() == null ||
                requestBookingDTO.getToddlers() == null ||
                requestBookingDTO.getHotelRooms() == null ||
                requestBookingDTO.getRoomCategory() == null ||
                requestBookingDTO.getCustomerName() == null || requestBookingDTO.getCustomerName().isBlank() ||
                requestBookingDTO.getCustomerEmail() == null || requestBookingDTO.getCustomerEmail().isBlank() ||
                requestBookingDTO.getCustomerPhone() == null || requestBookingDTO.getCustomerPhone().isBlank()) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "Missing required fields", null);
        }
        var user = userRepository.findUserById(requestBookingDTO.getUserId()).orElse(null);
        if (user == null) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "User not found", null);
        }
        var depart = locationRepository.findById(requestBookingDTO.getDepartureLocationId()).orElse(null);
        if (depart == null) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "Departure location not found", null);
        }
        var destinations = locationRepository.findAllById(requestBookingDTO.getDestinationLocationIds());

        RequestBooking requestBooking = requestBookingMapper.toEntity(requestBookingDTO);
        requestBooking.setUser(user);
        requestBooking.setDepartureLocation(depart);
        requestBooking.setDestinationLocations(destinations);
        requestBooking.setStatus(RequestBookingStatus.PENDING);
        RequestBooking saved = requestBookingRepository.save(requestBooking);
        notifyNewRequestBooking(saved);
        RequestBookingDTO savedDto = requestBookingMapper.toDTO(saved);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Request saved", savedDto);
    }

    @Override
    public GeneralResponse<RequestBookingDTO> updateStatus(Long id, ChangeStatusDTO changeStatusDTO) {
        RequestBooking booking = requestBookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return new GeneralResponse<>(HttpStatus.NOT_FOUND.value(), "Request not found", null);
        }
        RequestBookingStatus newStatus;
        try {
            newStatus = RequestBookingStatus.valueOf(changeStatusDTO.getNewStatus());
        } catch (IllegalArgumentException e) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid status", null);
        }
        booking.setStatus(newStatus);
        RequestBooking saved = requestBookingRepository.save(booking);
        RequestBookingDTO dto = requestBookingMapper.toDTO(saved);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Status updated", dto);
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

    @Override
    public GeneralResponse<PagingDTO<RequestBookingNotificationDTO>> getRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<RequestBooking> requests = requestBookingRepository.findAll(pageable);

        List<RequestBookingNotificationDTO> items = requests.getContent().stream()
                .map(this::toNotificationDTO)
                .collect(Collectors.toList());

        PagingDTO<RequestBookingNotificationDTO> pagingDTO = PagingDTO.<RequestBookingNotificationDTO>builder()
                .page(requests.getNumber())
                .size(requests.getSize())
                .total(requests.getTotalElements())
                .items(items)
                .build();

        return GeneralResponse.of(pagingDTO);
    }

    private void notifyNewRequestBooking(RequestBooking requestBooking) {
        RequestBookingNotificationDTO dto = toNotificationDTO(requestBooking);
        messagingTemplate.convertAndSend("/topic/request-bookings", dto);
    }

    private RequestBookingNotificationDTO toNotificationDTO(RequestBooking requestBooking) {
        return RequestBookingNotificationDTO.builder()
                .id(requestBooking.getId())
                .customerName(requestBooking.getCustomerName())
                .customerPhone(requestBooking.getCustomerPhone())
                .customerEmail(requestBooking.getCustomerEmail())
                .destinations(requestBooking.getDestinationLocations() != null ? requestBooking.getDestinationLocations().stream().map(loc -> loc.getName()).collect(Collectors.toList()) : null)
                .startDate(requestBooking.getStartDate())
                .endDate(requestBooking.getEndDate())
                .status(requestBooking.getStatus())
                .createdAt(requestBooking.getCreatedAt())
                .detailUrl(frontendBaseUrl + "/request-bookings/" + requestBooking.getId())
                .build();
    }
}