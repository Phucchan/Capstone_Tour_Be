package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingDetailDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.BookingCustomerRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.service.SellerBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerBookingServiceImpl implements SellerBookingService {
    private final BookingRepository bookingRepository;
    private final BookingCustomerRepository bookingCustomerRepository;
    private final UserRepository userRepository;
    private final TourDayRepository tourDayRepository;
    private final TourScheduleRepository tourScheduleRepository;


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
    @Override
    public GeneralResponse<SellerBookingDetailDTO> getBookingDetail(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        SellerBookingDetailDTO dto = toDetailDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }

    @Override
    public GeneralResponse<SellerBookingDetailDTO> updateBookingSchedule(Long bookingId, Long scheduleId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        var newSchedule = tourScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Schedule not found"));

        if (!newSchedule.getTour().getId().equals(booking.getTourSchedule().getTour().getId())) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Schedule does not belong to the same tour");
        }

        booking.setTourSchedule(newSchedule);
        bookingRepository.save(booking);

        SellerBookingDetailDTO dto = toDetailDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }

    private SellerBookingDetailDTO toDetailDTO(Booking booking) {
        var tour = booking.getTourSchedule().getTour();

        List<String> destinations = tourDayRepository.findByTourIdOrderByDayNumberAsc(tour.getId())
                .stream()
                .filter(day -> day.getLocation() != null)
                .map(day -> day.getLocation().getName())
                .toList();

        List<String> themes = tour.getThemes() != null
                ? tour.getThemes().stream().map(th -> th.getName()).toList()
                : java.util.Collections.emptyList();

        int totalSeats = booking.getTourSchedule().getTourPax().getMaxQuantity();
        int soldSeats = bookingRepository.sumGuestsByTourScheduleId(booking.getTourSchedule().getId());
        int remainingSeats = Math.max(totalSeats - soldSeats, 0);

        // Fetch upcoming schedules of the same tour
        List<TourScheduleDTO> scheduleDTOs = tourScheduleRepository
                .findByTourIdAndDepartureDateAfterOrderByDepartureDateAsc(
                        tour.getId(), LocalDateTime.now())
                .stream()
                .map(s -> {
                    int sold = bookingRepository.sumGuestsByTourScheduleId(s.getId());
                    int avail = Math.max(s.getTourPax().getMaxQuantity() - sold, 0);
                    return TourScheduleDTO.builder()
                            .id(s.getId())
                            .departureDate(s.getDepartureDate())
                            .endDate(s.getEndDate())
                            .price(s.getTourPax().getSellingPrice())
                            .extraHotelCost(s.getTourPax().getExtraHotelCost())
                            .availableSeats(avail)
                            .build();
                })
                .toList();

        return SellerBookingDetailDTO.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .tourName(tour.getName())
                .createdAt(booking.getCreatedAt())
                .status(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .operator(booking.getTourSchedule().getCoordinator().getFullName())
                .departureDate(booking.getTourSchedule().getDepartureDate())
                .tourType(tour.getTourType() != null ? tour.getTourType().name() : null)
                .themes(themes)
                .durationDays(tour.getDurationDays())
                .departLocation(tour.getDepartLocation() != null ? tour.getDepartLocation().getName() : null)
                .destinations(destinations)
                .totalSeats(totalSeats)
                .soldSeats(soldSeats)
                .remainingSeats(remainingSeats)
                .schedules(scheduleDTOs)
                .build();
    }

    private SellerBookingSummaryDTO toSummaryDTO(Booking booking) {
        return SellerBookingSummaryDTO.builder()
                .id(booking.getId())
                .tourName(booking.getTourSchedule().getTour().getName())
                .departureDate(booking.getTourSchedule().getDepartureDate())
                .bookingCode(booking.getBookingCode())
                .seats(
                        (booking.getAdults() != null ? booking.getAdults() : 0)
                                + (booking.getChildren() != null ? booking.getChildren() : 0))
                .customer(booking.getUser().getFullName())
                .status(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .build();
    }
}