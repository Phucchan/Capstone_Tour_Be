package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeStatusDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.RequestBookingNotificationDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourThemeOptionDTO;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.RequestBookingRepository;
import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
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
    private final TourThemeRepository tourThemeRepository;
    private final LocationRepository locationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RequestBookingVerificationService verificationService;

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
                requestBookingDTO.getTourThemeIds() == null || requestBookingDTO.getTourThemeIds().isEmpty() ||
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
                requestBookingDTO.getCustomerPhone() == null || requestBookingDTO.getCustomerPhone().isBlank() ||
                requestBookingDTO.getVerificationCode() == null || requestBookingDTO.getVerificationCode().isBlank()) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "Missing required fields", null);
        }
        if (!verificationService.verifyCode(requestBookingDTO.getCustomerEmail(), requestBookingDTO.getVerificationCode())) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid verification code", null);
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
        var themes = tourThemeRepository.findAllById(requestBookingDTO.getTourThemeIds());
        if (themes.size() != requestBookingDTO.getTourThemeIds().size()) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "One or more tour themes not found", null);
        }

        RequestBooking requestBooking = requestBookingMapper.toEntity(requestBookingDTO);
        requestBooking.setUser(user);
        requestBooking.setDepartureLocation(depart);
        requestBooking.setTourTheme(themes.stream().map(t -> t.getName()).collect(Collectors.joining(", ")));
        requestBooking.setDestinationLocations(destinations);
        requestBooking.setStatus(RequestBookingStatus.PENDING);
        requestBooking.setReason(null);
        RequestBooking saved = requestBookingRepository.save(requestBooking);
        notifyNewRequestBooking(saved);
        RequestBookingDTO savedDto = requestBookingMapper.toDTO(saved);
        savedDto.setTourThemeIds(requestBookingDTO.getTourThemeIds());
        return new GeneralResponse<>(HttpStatus.OK.value(), "Request saved", savedDto);
    }
    @Override
    public GeneralResponse<String> sendVerificationCode(String email) {
        if (email == null || email.isBlank()) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "Email is required", null);
        }
        verificationService.sendCode(email);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Verification code sent", null);
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
        if (newStatus != RequestBookingStatus.REJECTED) {
            booking.setReason(null);
        }
        booking.setStatus(newStatus);
        RequestBooking saved = requestBookingRepository.save(booking);
        RequestBookingDTO dto = requestBookingMapper.toDTO(saved);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Status updated", dto);
    }

    @Override
    public GeneralResponse<RequestBookingDTO> rejectRequest(Long id, String reason) {
        RequestBooking booking = requestBookingRepository.findById(id).orElse(null);
        if (booking == null) {
            return new GeneralResponse<>(HttpStatus.NOT_FOUND.value(), "Request not found", null);
        }
        if (reason == null || reason.isBlank()) {
            return new GeneralResponse<>(HttpStatus.BAD_REQUEST.value(), "Reason is required", null);
        }
        booking.setStatus(RequestBookingStatus.REJECTED);
        booking.setReason(reason);
        RequestBooking saved = requestBookingRepository.save(booking);
        RequestBookingDTO dto = requestBookingMapper.toDTO(saved);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Request rejected", dto);
    }
    @Override
    public GeneralResponse<List<TourThemeOptionDTO>> getTourThemes() {
        List<TourThemeOptionDTO> themes = tourThemeRepository.findAll().stream()
                .map(t -> new TourThemeOptionDTO(t.getId(), t.getName()))
                .collect(Collectors.toList());
        return GeneralResponse.of(themes);
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
    public GeneralResponse<PagingDTO<RequestBookingSummaryDTO>> getRequestsByUser(Long userId, int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RequestBooking> requestPage;
        if (search != null && !search.isBlank()) {
            requestPage = requestBookingRepository.searchByUserAndKeyword(userId, search.trim().toLowerCase(), pageable);
        } else {
            requestPage = requestBookingRepository.findByUser_Id(userId, pageable);
        }

        List<RequestBookingSummaryDTO> items = requestPage.getContent().stream()
                .map(rb -> RequestBookingSummaryDTO.builder()
                        .id(rb.getId())
                        .customerName(rb.getCustomerName())
                        .customerPhone(rb.getCustomerPhone())
                        .startDate(rb.getStartDate())
                        .endDate(rb.getEndDate())
                        .status(rb.getStatus())
                        .createdAt(rb.getCreatedAt())
                        .reason(rb.getReason())
                        .build())
                .collect(Collectors.toList());

        PagingDTO<RequestBookingSummaryDTO> pagingDTO = PagingDTO.<RequestBookingSummaryDTO>builder()
                .page(requestPage.getNumber())
                .size(requestPage.getSize())
                .total(requestPage.getTotalElements())
                .items(items)
                .build();

        return GeneralResponse.of(pagingDTO);
    }

    @Override
    public GeneralResponse<PagingDTO<RequestBookingNotificationDTO>> getRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<RequestBooking> requests = requestBookingRepository.findByStatus(RequestBookingStatus.ACCEPTED, pageable);

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
    @Override
    public GeneralResponse<PagingDTO<RequestBookingSummaryDTO>> getRequestsByStatus(RequestBookingStatus status,
                                                                                    int page,
                                                                                    int size,
                                                                                    String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RequestBooking> requestPage;
        if (search != null && !search.isBlank()) {
            requestPage = requestBookingRepository.searchByStatusAndKeyword(status,
                    search.trim().toLowerCase(), pageable);
        } else {
            requestPage = requestBookingRepository.findByStatus(status, pageable);
        }

        List<RequestBookingSummaryDTO> items = requestPage.getContent().stream()
                .map(rb -> RequestBookingSummaryDTO.builder()
                        .id(rb.getId())
                        .customerName(rb.getCustomerName())
                        .customerPhone(rb.getCustomerPhone())
                        .startDate(rb.getStartDate())
                        .endDate(rb.getEndDate())
                        .status(rb.getStatus())
                        .createdAt(rb.getCreatedAt())
                        .reason(rb.getReason())
                        .build())
                .collect(Collectors.toList());

        PagingDTO<RequestBookingSummaryDTO> pagingDTO = PagingDTO.<RequestBookingSummaryDTO>builder()
                .page(requestPage.getNumber())
                .size(requestPage.getSize())
                .total(requestPage.getTotalElements())
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