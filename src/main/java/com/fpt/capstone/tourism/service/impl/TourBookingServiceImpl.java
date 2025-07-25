package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.common.tour.TourScheduleShortInfoDTO;
import com.fpt.capstone.tourism.dto.common.tour.TourShortInfoDTO;
import com.fpt.capstone.tourism.dto.common.user.BookedPersonDTO;
import com.fpt.capstone.tourism.dto.common.user.TourCustomerDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestDTO;
import com.fpt.capstone.tourism.dto.response.booking.BookingConfirmResponse;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.BookingHelper;
import com.fpt.capstone.tourism.mapper.TourDetailMapper;
import com.fpt.capstone.tourism.mapper.booking.BookingCustomerMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.domain.projection.PartnerServiceWithDayDTO;
import com.fpt.capstone.tourism.model.enums.*;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.payment.*;
import com.fpt.capstone.tourism.model.tour.*;
import com.fpt.capstone.tourism.repository.BookingCustomerRepository;
import com.fpt.capstone.tourism.repository.booking.BookingServiceRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.service.VNPayService;
import com.fpt.capstone.tourism.service.payment.PaymentBillItemRepository;
import com.fpt.capstone.tourism.service.payment.PaymentBillRepository;
import com.fpt.capstone.tourism.service.tourbooking.TourBookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class TourBookingServiceImpl implements TourBookingService {

    private final BookingHelper bookingHelper;
    private final VNPayService vnPayService;
    private final BookingRepository bookingRepository;
    private final BookingCustomerRepository bookingCustomerRepository;
    private final BookingCustomerMapper bookingCustomerMapper;
    private final PartnerServiceRepository partnerServiceRepository;
    private final BookingServiceRepository bookingServiceRepository;
    private final PaymentBillRepository paymentBillRepository;
    private final PaymentBillItemRepository paymentBillItemRepository;
    private final TourDetailMapper tourDetailMapper;

    @Value("${backend.base-url}")
    private String backendBaseUrl;

    @Override
    @Transactional
    public String createBooking(BookingRequestDTO bookingRequestDTO) {
        try {
            List<BookingRequestCustomerDTO> allCustomersDTO = Stream.of(bookingRequestDTO.getAdults(), bookingRequestDTO.getChildren(), bookingRequestDTO.getInfants(), bookingRequestDTO.getToddlers())
                    .filter(Objects::nonNull)           // lọc ra list null
                    .flatMap(List::stream)              // gộp các list thành stream duy nhất
                    .toList();      // thu về một danh sách

            List<BookingCustomer> allCustomers = bookingCustomerMapper.toEntity(allCustomersDTO);

            String baseUrl = backendBaseUrl + "/public/booking";

            String bookingCode = bookingHelper.generateBookingCode(bookingRequestDTO.getTourId(), bookingRequestDTO.getScheduleId(), bookingRequestDTO.getUserId());

            String paymentUrl = vnPayService.generatePaymentUrl(bookingRequestDTO.getTotal(), bookingCode, baseUrl, 120);

            Booking tourBooking = Booking.builder()
                    .tourSchedule(TourSchedule.builder().id(bookingRequestDTO.getScheduleId()).build())
                    .note(bookingRequestDTO.getNote())
                    .bookingCode(bookingCode)
                    .user(User.builder().id(bookingRequestDTO.getUserId()).build())
                    .bookingStatus(BookingStatus.PENDING)
                    .sellingPrice(bookingRequestDTO.getSellingPrice())
                    .extraHotelCost(bookingRequestDTO.getExtraHotelCost())
                    .paymentMethod(bookingRequestDTO.getPaymentMethod())
                    .paymentUrl(paymentUrl)
                    .expiredAt(LocalDateTime.now().plusHours(9))
                    .totalAmount(bookingRequestDTO.getTotal())
                    .needHelp(bookingRequestDTO.isNeedHelp())
                    .adults(bookingRequestDTO.getAdults() != null ? bookingRequestDTO.getAdults().size() : 0)
                    .children(bookingRequestDTO.getChildren() != null ? bookingRequestDTO.getChildren().size() : 0)
                    .infants(bookingRequestDTO.getInfants() != null ? bookingRequestDTO.getInfants().size() : 0)
                    .toddlers(bookingRequestDTO.getToddlers() != null ? bookingRequestDTO.getToddlers().size() : 0)
                    .singleRooms(bookingRequestDTO.getNumberSingleRooms())
                    .build();


            Booking result = bookingRepository.save(tourBooking);

            for (BookingCustomer customer : allCustomers) {
                customer.setBooking(result);
            }

            BookingCustomer bookedPerson = BookingCustomer.builder()
                    .paxType(PaxType.ADULT)
                    .fullName(bookingRequestDTO.getFullName())
                    .email(bookingRequestDTO.getEmail())
                    .phoneNumber(bookingRequestDTO.getPhone())
                    .bookedPerson(true)
                    .booking(result)
                    .address(bookingRequestDTO.getAddress())
                    .build();

            allCustomers.add(bookedPerson);

            bookingCustomerRepository.saveAll(allCustomers);

            saveTourBookingService(result, allCustomers.size());
            createReceiptBookingBill(result, bookingRequestDTO.getTotal(), bookingRequestDTO.getFullName(), bookingRequestDTO.getPaymentMethod());

            return bookingCode;

        } catch (Exception ex) {
            throw BusinessException.of("Tạo Tour Booking Thất Bại", ex);
        }
    }

    @Override
    @Transactional
    public void saveTourBookingService(Booking booking, int totalCustomers) {
        TourSchedule tourSchedule = booking.getTourSchedule();
        List<PartnerServiceWithDayDTO> dtos = partnerServiceRepository.findServicesWithDayNumberByScheduleId(tourSchedule.getId());

        List<BookingService> bookingServices = dtos.stream()
                .map(dto -> BookingService.builder()
                        .booking(booking)
                        .service(PartnerService.builder().id(dto.getServiceId()).build())
                        .dayNumber(dto.getDayNumber())
                        .quantity(totalCustomers)
                        .status(BookingServiceStatus.CONFIRMED)
                        .build())
                .toList();

        bookingServiceRepository.saveAll(bookingServices);
    }

    @Override
    @Transactional
    public void createReceiptBookingBill(Booking tourBooking, Double total, String fullName, PaymentMethod paymentMethod) {

        PaymentBill transaction = PaymentBill.builder()
                .billNumber("PB-" + UUID.randomUUID().toString().substring(0, 8))
                .paymentForType(PaymentForType.COMPANY)
                .payTo("Đi Đâu Company")
                .bookingCode(tourBooking.getBookingCode())
                .paidBy(fullName)
                .creator(User.builder().id(tourBooking.getUser().getId()).build())
                .receiverAddress("Đi Đâu Company, 123 Đường ABC, Quận 1, TP.HCM")
                .totalAmount(BigDecimal.valueOf(total))
                .note("Customer pay for Booking Code: " + tourBooking.getBookingCode())
                .paymentMethod(paymentMethod)
                .paymentType(PaymentType.RECEIPT)
                .build();

        PaymentBill result = paymentBillRepository.save(transaction);

        PaymentBillItem paymentBillItem = PaymentBillItem.builder()
                .amount(BigDecimal.valueOf(total))
                .paymentBill(result)
                .content("Customer pay for Booking Code: " + tourBooking.getBookingCode())
                .discount(0)
                .amount(BigDecimal.valueOf(total))
                .quantity(1)
                .paymentBillItemStatus(PaymentBillItemStatus.PENDING)
                .build();

        paymentBillItemRepository.save(paymentBillItem);
    }

    @Override
    @Transactional
    public void confirmPayment(int paymentStatus, String orderInfo) {
        try {
            Booking tourBooking = bookingRepository.findByBookingCode(orderInfo);
            if (paymentStatus == 1) {
                tourBooking.setBookingStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(tourBooking);

                List<PaymentBillItem> paymentBillItems = paymentBillItemRepository.findAllByBookingCode(tourBooking.getBookingCode());

                for (PaymentBillItem paymentBillItem : paymentBillItems) {
                    paymentBillItem.setPaymentBillItemStatus(PaymentBillItemStatus.PAID);
                }

                paymentBillItemRepository.saveAll(paymentBillItems);

            } else {
                String baseUrl = backendBaseUrl + "/public/booking";
                // Nếu thanh toán không thành công, tạo lại paymentUrl
                int minuteExpired = tourBooking.getExpiredAt() != null ? (int) Duration.between(LocalDateTime.now(), tourBooking.getExpiredAt()).toMinutes() : 120;
                String paymentUrl = vnPayService.generatePaymentUrl(tourBooking.getTotalAmount(), tourBooking.getBookingCode(), baseUrl, minuteExpired);
                tourBooking.setPaymentUrl(paymentUrl);
                bookingRepository.save(tourBooking);
            }
        } catch (Exception ex) {
            throw BusinessException.of("Thanh toán booking thất bại", ex);
        }
    }

    @Override
    public BookingConfirmResponse getTourBookingDetails(String bookingCode) {
        try {
            Booking tourBooking = bookingRepository.findByBookingCode(bookingCode);


            TourShortInfoDTO tourShortInfoDTO = tourDetailMapper.toTourShortInfoDTO(tourBooking.getTourSchedule().getTour());
            TourScheduleShortInfoDTO tourScheduleShortInfoDTO = tourDetailMapper.toTourScheduleShortInfoDTO(tourBooking.getTourSchedule());

            List<BookingCustomer> adultEntities = bookingCustomerRepository.findByBooking_Id(tourBooking.getId());

            BookingCustomer bookedPerson = adultEntities.stream()
                    .filter(BookingCustomer::isBookedPerson)
                    .findFirst()
                    .orElseThrow(() -> BusinessException.of("Không tìm thấy người đã đặt tour"));

            BookedPersonDTO bookedPersonDTO = bookingCustomerMapper.toBookedPersonDTO(bookedPerson);


            List<TourCustomerDTO> adults = adultEntities.stream().filter(customer -> customer.getPaxType().equals(PaxType.ADULT) && !customer.isBookedPerson()).map(bookingCustomerMapper::toDTO).toList();
            List<TourCustomerDTO> children = adultEntities.stream().filter(customer -> customer.getPaxType().equals(PaxType.CHILD)).map(bookingCustomerMapper::toDTO).toList();
            List<TourCustomerDTO> infants = adultEntities.stream().filter(customer -> customer.getPaxType().equals(PaxType.INFANT)).map(bookingCustomerMapper::toDTO).toList();
            List<TourCustomerDTO> toddlers = adultEntities.stream().filter(customer -> customer.getPaxType().equals(PaxType.TODDLER)).map(bookingCustomerMapper::toDTO).toList();

            return BookingConfirmResponse.builder()
                    .id(tourBooking.getId())
                    .bookedPerson(bookedPersonDTO)
                    .tour(tourShortInfoDTO)
                    .tourSchedule(tourScheduleShortInfoDTO)
                    .adults(adults)
                    .sellingPrice(tourBooking.getSellingPrice())
                    .extraHotelCost(tourBooking.getExtraHotelCost())
                    .children(children)
                    .infants(infants)
                    .toddlers(toddlers)
                    .note(tourBooking.getNote())
                    .bookingCode(tourBooking.getBookingCode())
                    .paymentMethod(tourBooking.getPaymentMethod())
                    .createdAt(tourBooking.getCreatedAt())
                    .paymentMethod(tourBooking.getPaymentMethod())
                    .paymentUrl(tourBooking.getPaymentUrl())
                    .status(tourBooking.getBookingStatus())
                    .needHelp(tourBooking.isNeedHelp())
                    .singleRooms(tourBooking.getSingleRooms())
                    .build();
        } catch (Exception ex) {
            throw BusinessException.of("Lấy Thông Tin Tour Thất Bại", ex);
        }
    }

}
