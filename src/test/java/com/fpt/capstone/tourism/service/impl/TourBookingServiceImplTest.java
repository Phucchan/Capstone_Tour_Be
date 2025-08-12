package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.BookingHelper;
import com.fpt.capstone.tourism.mapper.booking.BookingCustomerMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.payment.PaymentBill;
import com.fpt.capstone.tourism.model.payment.PaymentBillItem;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.model.tour.TourSchedule;
import com.fpt.capstone.tourism.repository.BookingCustomerRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.booking.BookingServiceRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import com.fpt.capstone.tourism.service.VNPayService;
import com.fpt.capstone.tourism.service.payment.PaymentBillItemRepository;
import com.fpt.capstone.tourism.service.payment.PaymentBillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourBookingServiceImplTest {

    @InjectMocks
    private TourBookingServiceImpl tourBookingService;

    // Mock tất cả các dependency của service
    @Mock private BookingHelper bookingHelper;
    @Mock private VNPayService vnPayService;
    @Mock private BookingRepository bookingRepository;
    @Mock private BookingCustomerRepository bookingCustomerRepository;
    @Mock private BookingCustomerMapper bookingCustomerMapper;
    @Mock private PartnerServiceRepository partnerServiceRepository;
    @Mock private BookingServiceRepository bookingServiceRepository;
    @Mock private PaymentBillRepository paymentBillRepository;
    @Mock private PaymentBillItemRepository paymentBillItemRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    private BookingRequestDTO validBookingRequest;

    @BeforeEach
    void setUp() {
        // Khởi tạo một request hợp lệ để tái sử dụng trong các test case
        validBookingRequest = BookingRequestDTO.builder()
                .tourId(1L)
                .scheduleId(10L)
                .userId(100L)
                .tourName("Khám phá Đà Nẵng")
                .fullName("Nguyễn Văn A")
                .email("nguyenvana@example.com")
                .phone("0987654321")
                .address("123 Đường ABC, Quận 1, TP.HCM")
                .note("Không ăn cay")
                .total(5000000.0)
                .sellingPrice(2500000.0)
                .adults(List.of(BookingRequestCustomerDTO.builder().fullName("Nguyễn Văn A").build()))
                .children(List.of(BookingRequestCustomerDTO.builder().fullName("Nguyễn Thị B").build()))
                .infants(Collections.emptyList())
                .toddlers(Collections.emptyList())
                .build();
    }

    // ===================================================
    // 1. Normal Case - Dữ liệu đầu vào hợp lệ
    // ===================================================

    @Test
    @DisplayName("[Normal] Tạo booking thành công khi tất cả dữ liệu đầu vào đều hợp lệ")
    void createBooking_whenRequestIsValid_shouldSucceed() {
        System.out.println("Test Case: Tạo booking thành công với dữ liệu hợp lệ.");
        // Arrange
        String expectedBookingCode = "DN-10-100-XYZ";

        // Giả lập các dependency trả về giá trị thành công
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn(expectedBookingCode);
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("https://vnpay.vn/payment-url");
        when(bookingCustomerMapper.toEntity(any())).thenReturn(new ArrayList<>());

        Booking savedBooking = Booking.builder().id(1L).user(new User()).tourSchedule(new TourSchedule()).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(paymentBillRepository.save(any(PaymentBill.class))).thenReturn(new PaymentBill());

        // Act
        String actualBookingCode = tourBookingService.createBooking(validBookingRequest);

        // Assert
        assertNotNull(actualBookingCode, "Booking code trả về không được là null.");
        assertEquals(expectedBookingCode, actualBookingCode, "Booking code trả về không khớp với mong đợi.");

        // Verify
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(vnPayService, times(1)).generatePaymentUrl(anyDouble(), eq(expectedBookingCode), anyString(), anyInt());
    }

    // ===================================================
    // 2. Abnormal Cases - Dữ liệu đầu vào không hợp lệ
    // ===================================================

    @Test
    @DisplayName("[Abnormal] Thất bại khi tourId là null")
    void createBooking_whenTourIdIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi tourId là null. Mong đợi BusinessException.");
        // Arrange
        validBookingRequest.setTourId(null);

        // Giả lập bookingHelper sẽ ném NPE khi nhận tourId là null
        when(bookingHelper.generateBookingCode(isNull(), anyLong(), anyLong())).thenThrow(NullPointerException.class);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.createBooking(validBookingRequest);
        });
        assertEquals(Constants.Message.CREATE_BOOKING_FAILED, exception.getMessage());
    }

    @Test
    @DisplayName("[Abnormal] Thất bại khi userId là null")
    void createBooking_whenUserIdIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi userId là null. Mong đợi BusinessException.");
        // Arrange
        validBookingRequest.setUserId(null);
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn("ANY-CODE");
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("ANY-URL");

        // Act & Assert
        // Lỗi sẽ xảy ra khi gọi User.builder().id(null).build() -> NPE
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.createBooking(validBookingRequest);
        });
        assertEquals(Constants.Message.CREATE_BOOKING_FAILED, exception.getMessage());
    }

    @Test
    @DisplayName("[Abnormal] Thất bại khi tổng giá tiền (total) là null")
    void createBooking_whenTotalIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi tổng giá tiền (total) là null. Mong đợi BusinessException.");
        // Arrange
        validBookingRequest.setTotal(null);
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn("ANY-CODE");

        // SỬA LỖI: Xóa dòng when(...) bị lỗi.
        // Lỗi NullPointerException sẽ tự động xảy ra bên trong hàm createBooking
        // khi Java cố gắng chuyển đổi một Double null thành double nguyên thủy.
        // Khối try-catch của service sẽ bắt lỗi này và chuyển thành BusinessException.

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.createBooking(validBookingRequest);
        });
        assertEquals(Constants.Message.CREATE_BOOKING_FAILED, exception.getMessage());
    }

    @Test
    @DisplayName("[Abnormal] Thất bại khi danh sách người lớn (adults) là null")
    void createBooking_whenAdultsListIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi danh sách người lớn (adults) là null. Mong đợi BusinessException.");
        // Arrange
        validBookingRequest.setAdults(null);
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn("ANY-CODE");
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("ANY-URL");

        // Act & Assert
        // Lỗi sẽ xảy ra khi gọi .flatMap(List::stream) trên một list null -> NPE
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.createBooking(validBookingRequest);
        });
        assertEquals(Constants.Message.CREATE_BOOKING_FAILED, exception.getMessage());
    }

    @Test
    @DisplayName("[Abnormal] Thất bại khi họ tên người đặt (fullName) là null")
    void createBooking_whenFullNameIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi họ tên người đặt (fullName) là null. Mong đợi BusinessException.");
        // Arrange
        validBookingRequest.setFullName(null);
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn("ANY-CODE");
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("ANY-URL");
        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());

        // Act & Assert
        // Lỗi sẽ xảy ra khi gọi .fullName(null) trong builder của BookingCustomer -> NPE
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.createBooking(validBookingRequest);
        });
        assertEquals(Constants.Message.CREATE_BOOKING_FAILED, exception.getMessage());
    }
}