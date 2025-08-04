package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.dto.request.seller.SellerBookingUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingCustomerDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingDetailDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourScheduleDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.enums.PaxType;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.model.tour.BookingCustomer;
import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.BookingCustomerRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.service.EmailService;
import com.fpt.capstone.tourism.service.SellerBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SellerBookingServiceImpl implements SellerBookingService {
    private final BookingRepository bookingRepository;
    private final BookingCustomerRepository bookingCustomerRepository;
    private final UserRepository userRepository;
    private final TourDayRepository tourDayRepository;
    private final TourScheduleRepository tourScheduleRepository;
    private final EmailService emailService;


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
    @Override
    @Transactional
    public GeneralResponse<SellerBookingDetailDTO> claimBooking(Long bookingId, String sellerUsername) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getSeller() != null) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Booking already assigned");
        }

        var seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Seller not found"));

        booking.setSeller(seller);
        bookingRepository.save(booking);

        SellerBookingDetailDTO dto = toDetailDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }
    @Override
    @Transactional
    public GeneralResponse<SellerBookingDetailDTO> updateBookedPerson(Long bookingId, SellerBookingUpdateRequestDTO requestDTO) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        BookingCustomer bookedPerson = bookingCustomerRepository
                .findFirstByBooking_IdAndBookedPersonTrue(bookingId);
        if (bookedPerson == null) {
            throw BusinessException.of(HttpStatus.NOT_FOUND, "Booked person not found");
        }

        if (requestDTO.getFullName() != null) bookedPerson.setFullName(requestDTO.getFullName());
        if (requestDTO.getAddress() != null) bookedPerson.setAddress(requestDTO.getAddress());
        if (requestDTO.getEmail() != null) bookedPerson.setEmail(requestDTO.getEmail());
        if (requestDTO.getPhone() != null) bookedPerson.setPhoneNumber(requestDTO.getPhone());

        if (requestDTO.getPaymentDeadline() != null) {
            booking.setExpiredAt(requestDTO.getPaymentDeadline());
        }

        bookingCustomerRepository.save(bookedPerson);
        bookingRepository.save(booking);

        SellerBookingDetailDTO dto = toDetailDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }
    @Override
    @Transactional
    public GeneralResponse<SellerBookingDetailDTO> updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        if (status != BookingStatus.CONFIRMED) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Invalid status");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Booking is not pending");
        }

        booking.setBookingStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Send confirmation email to the booked person
        BookingCustomer bookedPerson = bookingCustomerRepository
                .findFirstByBooking_IdAndBookedPersonTrue(booking.getId());
        if (bookedPerson != null && bookedPerson.getEmail() != null) {
            double total = booking.getTotalAmount();
            double deposit = total * 0.7;
            double remaining = total - deposit;

            var tour = booking.getTourSchedule().getTour();
            var days = tourDayRepository.findByTourIdOrderByDayNumberAsc(tour.getId());
            List<String> destinations = days.stream()
                    .map(d -> d.getLocation() != null ? d.getLocation().getName() : null)
                    .filter(Objects::nonNull)
                    .toList();
            Set<String> services = days.stream()
                    .flatMap(d -> d.getServiceTypes().stream())
                    .map(st -> st.getName())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            int adults = booking.getAdults() != null ? booking.getAdults() : 0;
            int children = booking.getChildren() != null ? booking.getChildren() : 0;
            int infants = booking.getInfants() != null ? booking.getInfants() : 0;
            int toddlers = booking.getToddlers() != null ? booking.getToddlers() : 0;
            int totalGuests = adults + children + infants + toddlers;

            String subject = "Booking confirmation for " + tour.getName();
            StringBuilder content = new StringBuilder()
                    .append("Hello ").append(bookedPerson.getFullName()).append(",\n\n")
                    .append("Your tour booking has been confirmed. Below are the details:\n\n")
                    .append("Tour: ").append(tour.getName()).append("\n")
                    .append("Group code: ").append(booking.getBookingCode()).append("\n")
                    .append("Departure: ").append(booking.getTourSchedule().getDepartureDate()).append(" from ")
                    .append(tour.getDepartLocation() != null ? tour.getDepartLocation().getName() : "N/A").append("\n")
                    .append("End date: ").append(booking.getTourSchedule().getEndDate()).append("\n\n")
                    .append("Group information:\n")
                    .append(" - Adults: ").append(adults).append("\n")
                    .append(" - Children: ").append(children).append("\n")
                    .append(" - Infants: ").append(infants).append("\n")
                    .append(" - Toddlers: ").append(toddlers).append("\n")
                    .append(" - Total guests: ").append(totalGuests).append("\n\n");

            if (!destinations.isEmpty()) {
                content.append("Destinations: ").append(String.join(", ", destinations)).append("\n");
            }
            if (!services.isEmpty()) {
                content.append("Services included: ").append(String.join(", ", services)).append("\n");
            }

            content.append("\nPayment details:\n")
                    .append(" - Total amount: ").append(String.format("%.2f", total)).append("\n")
                    .append(" - Deposit (70%): ").append(String.format("%.2f", deposit)).append(" due by ")
                    .append(booking.getExpiredAt()).append("\n")
                    .append(" - Remaining (30%): ").append(String.format("%.2f", remaining))
                    .append(" payable after the tour\n\n")
                    .append("If you have any questions, please contact us.\n")
                    .append("Thank you for choosing our service.");

            emailService.sendEmail(bookedPerson.getEmail(), subject, content.toString());
        }

        SellerBookingDetailDTO dto = toDetailDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }
    @Override
    @Transactional
    public GeneralResponse<SellerBookingDetailDTO> updateCustomer(Long customerId, BookingRequestCustomerDTO requestDTO) {
        BookingCustomer customer = bookingCustomerRepository.findById(customerId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Customer not found"));

        if (customer.isBookedPerson()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Cannot update booked person");
        }

        Booking booking = bookingRepository.findByIdForUpdate(customer.getBooking().getId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        PaxType oldType = customer.getPaxType();
        boolean oldSingle = customer.isSingleRoom();

        customer.setFullName(requestDTO.getFullName());
        customer.setGender(requestDTO.getGender());
        customer.setDateOfBirth(requestDTO.getDateOfBirth());
        customer.setSingleRoom(requestDTO.isSingleRoom());
        customer.setPaxType(requestDTO.getPaxType());
        customer.setEmail(requestDTO.getEmail());
        customer.setPhoneNumber(requestDTO.getPhoneNumber());
        customer.setAddress(requestDTO.getAddress());
        customer.setPickUpAddress(requestDTO.getPickUpAddress());
        customer.setNote(requestDTO.getNote());

        adjustBookingOnUpdate(booking, oldType, customer.getPaxType(), oldSingle, customer.isSingleRoom());

        bookingCustomerRepository.save(customer);
        bookingRepository.save(booking);

        SellerBookingDetailDTO dto = toDetailDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }

    @Override
    @Transactional
    public GeneralResponse<SellerBookingDetailDTO> deleteCustomer(Long customerId) {
        BookingCustomer customer = bookingCustomerRepository.findById(customerId)
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Customer not found"));

        if (customer.isBookedPerson()) {
            throw BusinessException.of(HttpStatus.BAD_REQUEST, "Cannot delete booked person");
        }

        Booking booking = bookingRepository.findByIdForUpdate(customer.getBooking().getId())
                .orElseThrow(() -> BusinessException.of(HttpStatus.NOT_FOUND, "Booking not found"));

        PaxType type = customer.getPaxType();
        boolean single = customer.isSingleRoom();

        customer.softDelete();

        adjustBookingOnDelete(booking, type, single);

        bookingCustomerRepository.save(customer);
        bookingRepository.save(booking);

        SellerBookingDetailDTO dto = toDetailDTO(booking);
        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", dto);
    }

    private void adjustBookingOnUpdate(Booking booking, PaxType oldType, PaxType newType,
                                       boolean oldSingle, boolean newSingle) {
        double pricePerPerson = booking.getSellingPrice() != null ? booking.getSellingPrice()
                : booking.getTourSchedule().getTourPax().getSellingPrice();
        double extraHotel = booking.getExtraHotelCost() != null ? booking.getExtraHotelCost()
                : booking.getTourSchedule().getTourPax().getExtraHotelCost();

        if (oldType != newType) {
            decrementPax(booking, oldType);
            incrementPax(booking, newType);
            double diff = priceFor(newType, pricePerPerson) - priceFor(oldType, pricePerPerson);
            booking.setTotalAmount(booking.getTotalAmount() + diff);
        }

        if (oldSingle != newSingle) {
            int current = booking.getSingleRooms() != null ? booking.getSingleRooms() : 0;
            if (newSingle) {
                booking.setSingleRooms(current + 1);
                booking.setTotalAmount(booking.getTotalAmount() + extraHotel);
            } else {
                booking.setSingleRooms(Math.max(current - 1, 0));
                booking.setTotalAmount(booking.getTotalAmount() - extraHotel);
            }
        }
    }

    private void adjustBookingOnDelete(Booking booking, PaxType type, boolean single) {
        double pricePerPerson = booking.getSellingPrice() != null ? booking.getSellingPrice()
                : booking.getTourSchedule().getTourPax().getSellingPrice();
        double extraHotel = booking.getExtraHotelCost() != null ? booking.getExtraHotelCost()
                : booking.getTourSchedule().getTourPax().getExtraHotelCost();

        decrementPax(booking, type);
        booking.setTotalAmount(booking.getTotalAmount() - priceFor(type, pricePerPerson));

        if (single) {
            int current = booking.getSingleRooms() != null ? booking.getSingleRooms() : 0;
            booking.setSingleRooms(Math.max(current - 1, 0));
            booking.setTotalAmount(booking.getTotalAmount() - extraHotel);
        }
    }

    private void decrementPax(Booking booking, PaxType type) {
        switch (type) {
            case ADULT -> booking.setAdults((booking.getAdults() != null ? booking.getAdults() : 0) - 1);
            case CHILD -> booking.setChildren((booking.getChildren() != null ? booking.getChildren() : 0) - 1);
            case INFANT -> booking.setInfants((booking.getInfants() != null ? booking.getInfants() : 0) - 1);
            case TODDLER -> booking.setToddlers((booking.getToddlers() != null ? booking.getToddlers() : 0) - 1);
        }
    }

    private void incrementPax(Booking booking, PaxType type) {
        switch (type) {
            case ADULT -> booking.setAdults((booking.getAdults() != null ? booking.getAdults() : 0) + 1);
            case CHILD -> booking.setChildren((booking.getChildren() != null ? booking.getChildren() : 0) + 1);
            case INFANT -> booking.setInfants((booking.getInfants() != null ? booking.getInfants() : 0) + 1);
            case TODDLER -> booking.setToddlers((booking.getToddlers() != null ? booking.getToddlers() : 0) + 1);
        }
    }

    private double priceFor(PaxType type, double basePrice) {
        return switch (type) {
            case CHILD -> basePrice * 0.75;
            case TODDLER -> basePrice * 0.5;
            case INFANT -> 0;
            case ADULT -> basePrice;
        };
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
        BookingCustomer bookedPerson = bookingCustomerRepository
                .findFirstByBooking_IdAndBookedPersonTrue(booking.getId());

        List<SellerBookingCustomerDTO> customerDTOs = bookingCustomerRepository.findByBooking_Id(booking.getId()).stream()
                .filter(c -> !c.isBookedPerson())
                .map(c -> SellerBookingCustomerDTO.builder()
                        .id(c.getId())
                        .fullName(c.getFullName())
                        .phoneNumber(c.getPhoneNumber())
                        .dateOfBirth(c.getDateOfBirth())
                        .gender(c.getGender())
                        .paxType(c.getPaxType())
                        .pickUpAddress(c.getPickUpAddress())
                        .singleRoom(c.isSingleRoom())
                        .note(c.getNote())
                        .status(c.getDeleted() != null && c.getDeleted() ? "DELETED" : "ACTIVE")
                        .build())
                .toList();

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
                .customerName(bookedPerson != null ? bookedPerson.getFullName() : null)
                .address(bookedPerson != null ? bookedPerson.getAddress() : null)
                .email(bookedPerson != null ? bookedPerson.getEmail() : null)
                .phoneNumber(bookedPerson != null ? bookedPerson.getPhoneNumber() : null)
                .paymentDeadline(booking.getExpiredAt())
                .tourName(tour.getName())
                .createdAt(booking.getCreatedAt())
                .status(booking.getBookingStatus() != null ? booking.getBookingStatus().name() : null)
                .paymentMethod(booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null)
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
                .customers(customerDTOs)
                .totalAmount(booking.getTotalAmount())
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