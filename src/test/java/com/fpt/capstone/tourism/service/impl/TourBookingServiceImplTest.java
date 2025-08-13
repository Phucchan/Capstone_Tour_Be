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
import com.fpt.capstone.tourism.model.tour.BookingCustomer;
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
import org.mockito.ArgumentCaptor;
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
    // 1. Normal Cases - Dữ liệu đầu vào hợp lệ
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

    @Test
    @DisplayName("[Normal] Tạo booking thành công khi ghi chú (note) là null")
    void createBooking_whenNoteIsNull_shouldSucceed() {
        System.out.println("Test Case: Tạo booking thành công khi ghi chú (note) là null.");
        // Arrange
        validBookingRequest.setNote(null); // Đặt note là null
        String expectedBookingCode = "DN-10-100-NONOTE";

        // Giả lập các dependency
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn(expectedBookingCode);
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("https://vnpay.vn/payment-url");
        when(bookingCustomerMapper.toEntity(any())).thenReturn(new ArrayList<>());

        // Sử dụng ArgumentCaptor để bắt đối tượng Booking được lưu
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        when(bookingRepository.save(bookingCaptor.capture())).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(1L); // Giả lập việc lưu và có ID trả về
            return savedBooking;
        });

        when(paymentBillRepository.save(any(PaymentBill.class))).thenReturn(new PaymentBill());

        // Act
        String actualBookingCode = tourBookingService.createBooking(validBookingRequest);

        // Assert
        assertNotNull(actualBookingCode);
        assertEquals(expectedBookingCode, actualBookingCode);

        // Kiểm tra đối tượng Booking đã được bắt
        Booking capturedBooking = bookingCaptor.getValue();
        assertNull(capturedBooking.getNote(), "Ghi chú trong booking đã lưu phải là null.");

        // Verify
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("[Normal] Tạo booking thành công khi địa chỉ (address) là null")
    void createBooking_whenAddressIsNull_shouldSucceed() {
        System.out.println("Test Case: Tạo booking thành công khi địa chỉ người đặt là null.");
        // Arrange
        validBookingRequest.setAddress(null); // Đặt address là null
        String expectedBookingCode = "DN-10-100-NOADDRESS";

        // Giả lập các dependency
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn(expectedBookingCode);
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("https://vnpay.vn/payment-url");
        when(bookingCustomerMapper.toEntity(any())).thenReturn(new ArrayList<>());

        Booking savedBooking = Booking.builder().id(1L).user(new User()).tourSchedule(new TourSchedule()).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Sử dụng ArgumentCaptor để bắt danh sách BookingCustomer được lưu
        ArgumentCaptor<List<BookingCustomer>> customerListCaptor = ArgumentCaptor.forClass(List.class);
        when(bookingCustomerRepository.saveAll(customerListCaptor.capture())).thenReturn(new ArrayList<>());

        when(paymentBillRepository.save(any(PaymentBill.class))).thenReturn(new PaymentBill());

        // Act
        String actualBookingCode = tourBookingService.createBooking(validBookingRequest);

        // Assert
        assertNotNull(actualBookingCode);
        assertEquals(expectedBookingCode, actualBookingCode);

        // Kiểm tra đối tượng BookingCustomer đã được bắt
        List<BookingCustomer> capturedCustomers = customerListCaptor.getValue();
        BookingCustomer bookedPerson = capturedCustomers.stream()
                .filter(BookingCustomer::isBookedPerson)
                .findFirst()
                .orElse(null);

        assertNotNull(bookedPerson, "Phải tìm thấy thông tin người đặt tour.");
        assertNull(bookedPerson.getAddress(), "Địa chỉ trong thông tin người đặt tour phải là null.");

        // Verify
        verify(bookingRepository, times(1)).save(any(Booking.class));
        verify(bookingCustomerRepository, times(1)).saveAll(any());
    }

    // ===================================================
    // 2. Abnormal Cases - Dữ liệu đầu vào hoặc xử lý có lỗi
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
    @DisplayName("[Abnormal] Thất bại khi scheduleId là null")
    void createBooking_whenScheduleIdIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi scheduleId là null. Mong đợi BusinessException.");
        // Arrange
        validBookingRequest.setScheduleId(null);
        // Không cần mock các dependency vì lỗi sẽ xảy ra sớm khi gọi TourSchedule.builder().id(null)

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

        // SỬA LỖI: Không mock dòng when(vnPayService...) vì nó sẽ gây lỗi test.
        // Hãy để lỗi NullPointerException xảy ra tự nhiên bên trong service.

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.createBooking(validBookingRequest);
        });
        assertEquals(Constants.Message.CREATE_BOOKING_FAILED, exception.getMessage());
    }

    @Test
    @DisplayName("[Normal] Tạo booking thành công khi danh sách người lớn (adults) là null")
    void createBooking_whenAdultsListIsNull_shouldSucceed() {
        System.out.println("Test Case: Tạo booking thành công khi danh sách người lớn (adults) là null.");
        // Arrange
        validBookingRequest.setAdults(null); // Trường hợp cần test
        // Giả sử booking vẫn hợp lệ nếu có children
        assertTrue(validBookingRequest.getChildren() != null && !validBookingRequest.getChildren().isEmpty(), "Test setup requires children to be present when adults are null");

        String expectedBookingCode = "DN-10-100-NOADULTS";
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn(expectedBookingCode);
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("ANY-URL");
        when(bookingCustomerMapper.toEntity(any())).thenReturn(new ArrayList<>());
        Booking savedBooking = Booking.builder().id(1L).user(new User()).tourSchedule(new TourSchedule()).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(paymentBillRepository.save(any(PaymentBill.class))).thenReturn(new PaymentBill());

        // Act & Assert
        // SỬA LỖI: Kỳ vọng là hàm sẽ chạy thành công vì service có .filter(Objects::nonNull)
        assertDoesNotThrow(() -> {
            String actualBookingCode = tourBookingService.createBooking(validBookingRequest);
            assertEquals(expectedBookingCode, actualBookingCode);
        });

        // Verify
        verify(bookingRepository, times(1)).save(any(Booking.class));
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

    @Test
    @DisplayName("[Abnormal] Thất bại khi lưu danh sách khách hàng (CustomerRepository) gặp lỗi")
    void createBooking_whenCustomerRepoFails_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi CustomerRepository.saveAll() ném ra lỗi. Mong đợi BusinessException.");
        // Arrange
        when(bookingHelper.generateBookingCode(anyLong(), anyLong(), anyLong())).thenReturn("ANY-CODE");
        when(vnPayService.generatePaymentUrl(anyDouble(), anyString(), anyString(), anyInt())).thenReturn("ANY-URL");
        when(bookingCustomerMapper.toEntity(any())).thenReturn(new ArrayList<>());
        Booking savedBooking = Booking.builder().id(1L).user(new User()).tourSchedule(new TourSchedule()).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        // Giả lập lỗi ở bước lưu danh sách khách hàng
        when(bookingCustomerRepository.saveAll(any())).thenThrow(new RuntimeException("Database error on saving customers"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.createBooking(validBookingRequest);
        });
        assertEquals(Constants.Message.CREATE_BOOKING_FAILED, exception.getMessage());

        // Verify
        // Hàm save của booking đã được gọi
        verify(bookingRepository, times(1)).save(any(Booking.class));
        // Nhưng các hàm sau đó (như lưu bill) thì không được gọi do transaction sẽ rollback
        verify(paymentBillRepository, never()).save(any(PaymentBill.class));
    }
}