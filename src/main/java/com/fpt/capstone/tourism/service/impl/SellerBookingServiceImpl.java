package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.seller.BookingCustomerUpdateDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.model.tour.BookingCustomer;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.BookingCustomerRepository;
import com.fpt.capstone.tourism.repository.tour.BookingRepository;
import com.fpt.capstone.tourism.service.SellerBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerBookingServiceImpl implements SellerBookingService {
    private final BookingRepository bookingRepository;
    private final BookingCustomerRepository bookingCustomerRepository;
    private final UserRepository userRepository;

    @Override
    public GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> getAvailableBookings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<Booking> bookingPage = bookingRepository.findBySellerIsNullOrderByCreatedAtAsc(pageable);

        List<SellerBookingSummaryDTO> dtos = bookingPage.getContent().stream()
                .map(this::toSummaryDTO)
                .toList();

        PagingDTO<SellerBookingSummaryDTO> paging = PagingDTO.<SellerBookingSummaryDTO>builder()
                .page(bookingPage.getNumber())
                .size(bookingPage.getSize())
                .total(bookingPage.getTotalElements())
                .items(dtos)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", paging);
    }

    @Override
    public GeneralResponse<Booking> updateBooking(Long bookingId, String sellerUsername,
                                                  List<BookingCustomerUpdateDTO> customers) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));
        User seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Seller not found"));

        if (customers != null) {
            for (BookingCustomerUpdateDTO dto : customers) {
                BookingCustomer customer = bookingCustomerRepository.findById(dto.getId())
                        .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking customer not found"));
                if (!customer.getBooking().getId().equals(bookingId)) {
                    throw BusinessException.of(HttpStatus.BAD_REQUEST, "Customer does not belong to booking");
                }
                customer.setFullName(dto.getFullName());
                customer.setAddress(dto.getAddress());
                customer.setEmail(dto.getEmail());
                customer.setDateOfBirth(dto.getDateOfBirth());
                customer.setPhoneNumber(dto.getPhoneNumber());
                customer.setPickUpAddress(dto.getPickUpAddress());
                customer.setGender(dto.getGender());
                customer.setPaxType(dto.getPaxType());
                customer.setSingleRoom(dto.isSingleRoom());
                bookingCustomerRepository.save(customer);
            }
        }

        booking.setSeller(seller);
        Booking saved = bookingRepository.save(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Updated", saved);
    }

    @Override
    public GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> getEditedTours(String sellerUsername, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Booking> bookingPage = bookingRepository.findBySeller_UsernameOrderByUpdatedAtDesc(sellerUsername, pageable);

        List<SellerBookingSummaryDTO> dtos = bookingPage.getContent().stream()
                .map(this::toSummaryDTO)
                .toList();

        PagingDTO<SellerBookingSummaryDTO> paging = PagingDTO.<SellerBookingSummaryDTO>builder()
                .page(bookingPage.getNumber())
                .size(bookingPage.getSize())
                .total(bookingPage.getTotalElements())
                .items(dtos)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", paging);
    }

    private SellerBookingSummaryDTO toSummaryDTO(Booking booking) {
        return SellerBookingSummaryDTO.builder()
                .tourName(booking.getTourSchedule().getTour().getName())
                .departureDate(booking.getTourSchedule().getDepartureDate())
                .bookingCode(booking.getBookingCode())
                .seats(booking.getAdults() + booking.getChildren())
                .customer(booking.getUser().getFullName())
                .status(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .build();
    }
}