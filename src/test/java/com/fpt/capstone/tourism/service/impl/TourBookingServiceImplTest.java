package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.BookingHelper;
import com.fpt.capstone.tourism.mapper.booking.BookingCustomerMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import com.fpt.capstone.tourism.model.payment.PaymentBill;
import com.fpt.capstone.tourism.model.payment.PaymentBillItem;
import com.fpt.capstone.tourism.model.payment.PaymentBillItemStatus;
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
import com.fpt.capstone.tourism.model.enums.PaxType;
import org.mockito.ArgumentCaptor;
import com.fpt.capstone.tourism.model.domain.projection.PartnerServiceWithDayDTO;
import com.fpt.capstone.tourism.model.enums.BookingServiceStatus;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.model.tour.BookingService;
import java.util.*;

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
    private Booking existingBooking;
    private List<BookingRequestCustomerDTO> newCustomersDTO;
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
        // --- Setup cho các test case của addCustomersToSchedule ---
        TourSchedule schedule = TourSchedule.builder().id(10L).build();
        existingBooking = Booking.builder()
                .id(1L)
                .tourSchedule(schedule)
                .adults(1)
                .children(0)
                .infants(0)
                .toddlers(0)
                .singleRooms(0)
                .sellingPrice(2000000.0)
                .extraHotelCost(500000.0)
                .totalAmount(2000000.0)
                .build();

        newCustomersDTO = Arrays.asList(
                BookingRequestCustomerDTO.builder().paxType(PaxType.ADULT).build(),
                BookingRequestCustomerDTO.builder().paxType(PaxType.CHILD).build()
        );
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

    // =================================================================
    // Test Cases for addCustomersToSchedule
    // =================================================================

    @Test
    @DisplayName("[addCustomers] Normal Case: Thêm khách hàng thành công với dữ liệu hợp lệ")
    void addCustomersToSchedule_whenRequestIsValid_shouldSucceed() {
        System.out.println("Test Case: Thêm khách hàng thành công với dữ liệu hợp lệ.");
        // Arrange
        Long bookingId = 1L;
        Long scheduleId = 10L;

        // Giả lập repository tìm thấy booking
        when(bookingRepository.findByIdForUpdate(bookingId)).thenReturn(Optional.of(existingBooking));

        // Giả lập mapper chuyển đổi DTO sang entity
        List<BookingCustomer> newCustomerEntities = Arrays.asList(
                BookingCustomer.builder().paxType(PaxType.ADULT).build(),
                BookingCustomer.builder().paxType(PaxType.CHILD).build()
        );
        when(bookingCustomerMapper.toEntity(newCustomersDTO)).thenReturn(newCustomerEntities);

        // Sử dụng ArgumentCaptor để bắt lại đối tượng Booking được lưu
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        // Act
        // Kỳ vọng hàm chạy mà không ném ra exception
        assertDoesNotThrow(() -> tourBookingService.addCustomersToSchedule(bookingId, scheduleId, newCustomersDTO));

        // Assert & Verify
        // Xác minh rằng hàm save của bookingRepository đã được gọi
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking updatedBooking = bookingCaptor.getValue();

        // Kiểm tra số lượng khách đã được cập nhật đúng
        // Ban đầu: 1 người lớn. Thêm: 1 người lớn, 1 trẻ em.
        assertEquals(2, updatedBooking.getAdults(), "Số lượng người lớn phải được cập nhật chính xác.");
        assertEquals(1, updatedBooking.getChildren(), "Số lượng trẻ em phải được cập nhật chính xác.");

        // Kiểm tra tổng tiền đã được tính lại
        // Tiền ban đầu: 2,000,000
        // Thêm 1 người lớn: 2,000,000
        // Thêm 1 trẻ em (75%): 1,500,000
        // Tổng mới mong đợi: 2,000,000 + 2,000,000 + 1,500,000 = 5,500,000
        assertEquals(5500000.0, updatedBooking.getTotalAmount(), "Tổng tiền phải được tính toán lại chính xác.");

        // Xác minh các hàm khác được gọi
        verify(bookingRepository, times(1)).findByIdForUpdate(bookingId);
        verify(bookingCustomerRepository, times(1)).saveAll(newCustomerEntities);
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[addCustomers] Abnormal Case: Thất bại khi không tìm thấy Booking")
    void addCustomersToSchedule_whenBookingNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi không tìm thấy Booking ID.");
        // Arrange
        Long nonExistentBookingId = 99L;
        Long scheduleId = 10L;

        // Giả lập repository không tìm thấy booking
        when(bookingRepository.findByIdForUpdate(nonExistentBookingId)).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.addCustomersToSchedule(nonExistentBookingId, scheduleId, newCustomersDTO);
        });

        // Kiểm tra thông báo lỗi
        assertEquals("Thêm khách hàng thất bại", exception.getMessage());
        System.out.println("Log: " + Constants.Message.BOOKING_NOT_FOUND);

        // Verify
        // Đảm bảo không có hành động lưu nào được thực hiện
        verify(bookingCustomerRepository, never()).saveAll(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("[addCustomers] Abnormal Case: Thất bại khi Schedule ID không khớp")
    void addCustomersToSchedule_whenScheduleIdMismatches_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi Schedule ID không khớp.");
        // Arrange
        Long bookingId = 1L;
        Long wrongScheduleId = 99L; // ID lịch trình sai

        // Giả lập repository tìm thấy booking, nhưng scheduleId của nó là 10L
        when(bookingRepository.findByIdForUpdate(bookingId)).thenReturn(Optional.of(existingBooking));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.addCustomersToSchedule(bookingId, wrongScheduleId, newCustomersDTO);
        });

        assertEquals("Thêm khách hàng thất bại", exception.getMessage());
        System.out.println("Log: Lỗi không khớp Schedule ID.");

        // Verify
        verify(bookingCustomerRepository, never()).saveAll(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("[addCustomers] Abnormal Case: Thất bại khi danh sách khách hàng truyền vào là null")
    void addCustomersToSchedule_whenCustomerListIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi danh sách khách hàng là null.");
        // Arrange
        Long bookingId = 1L;
        Long scheduleId = 10L;

        // Giả lập repository tìm thấy booking
        when(bookingRepository.findByIdForUpdate(bookingId)).thenReturn(Optional.of(existingBooking));

        // Giả lập mapper sẽ ném lỗi khi nhận list null
        when(bookingCustomerMapper.toEntity(null)).thenThrow(NullPointerException.class);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.addCustomersToSchedule(bookingId, scheduleId, null); // Truyền vào list null
        });

        assertEquals("Thêm khách hàng thất bại", exception.getMessage());
        System.out.println("Log: " + Constants.Message.GET_DATA_FAIL);

        // Verify
        verify(bookingCustomerRepository, never()).saveAll(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("[addCustomers] Abnormal Case: Thất bại khi database gặp lỗi lúc lưu")
    void addCustomersToSchedule_whenDatabaseFailsOnSave_shouldThrowBusinessException() {
        System.out.println("Test Case: Thất bại khi database gặp lỗi lúc lưu.");
        // Arrange
        Long bookingId = 1L;
        Long scheduleId = 10L;

        when(bookingRepository.findByIdForUpdate(bookingId)).thenReturn(Optional.of(existingBooking));
        when(bookingCustomerMapper.toEntity(newCustomersDTO)).thenReturn(new ArrayList<>());

        // Giả lập database ném ra lỗi khi lưu danh sách khách hàng
        doThrow(new RuntimeException("Database connection error")).when(bookingCustomerRepository).saveAll(any());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            tourBookingService.addCustomersToSchedule(bookingId, scheduleId, newCustomersDTO);
        });

        assertEquals("Thêm khách hàng thất bại", exception.getMessage());
        System.out.println("Log: " + Constants.Message.GENERAL_FAIL_MESSAGE);

        // Verify
        // Đảm bảo hàm save của booking không được gọi do transaction sẽ rollback
        verify(bookingRepository, never()).save(any());
    }
    @Test
    @DisplayName("[saveTourBookingService] Normal Case: Lưu dịch vụ thành công khi lịch trình có dịch vụ")
    void saveTourBookingService_whenScheduleHasServices_shouldSaveBookingServices() {
        System.out.println("Test Case: Lưu dịch vụ thành công khi lịch trình có dịch vụ.");
        // Arrange
        // 1. Dữ liệu đầu vào
        TourSchedule schedule = TourSchedule.builder().id(10L).build();
        Booking booking = Booking.builder().id(1L).tourSchedule(schedule).build();
        int totalCustomers = 5;

        // 2. Giả lập repository trả về một danh sách dịch vụ
        // SỬA LỖI: Tạo mock cho interface PartnerServiceWithDayDTO
        PartnerServiceWithDayDTO dto1 = mock(PartnerServiceWithDayDTO.class);
        when(dto1.getServiceId()).thenReturn(101L);
        when(dto1.getDayNumber()).thenReturn(1);

        PartnerServiceWithDayDTO dto2 = mock(PartnerServiceWithDayDTO.class);
        when(dto2.getServiceId()).thenReturn(102L);
        when(dto2.getDayNumber()).thenReturn(2);

        List<PartnerServiceWithDayDTO> serviceDTOs = List.of(dto1, dto2);
        when(partnerServiceRepository.findServicesWithDayNumberByScheduleId(10L)).thenReturn(serviceDTOs);

        // 3. Sử dụng ArgumentCaptor để "bắt" lại danh sách được lưu
        ArgumentCaptor<List<BookingService>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        tourBookingService.saveTourBookingService(booking, totalCustomers);

        // Assert & Verify
        // 4. Xác minh rằng hàm saveAll đã được gọi đúng 1 lần
        verify(bookingServiceRepository, times(1)).saveAll(captor.capture());

        // 5. Kiểm tra nội dung của danh sách đã được "bắt"
        List<BookingService> savedServices = captor.getValue();
        assertEquals(2, savedServices.size(), "Số lượng dịch vụ được lưu phải bằng 2.");

        BookingService firstService = savedServices.get(0);
        assertEquals(booking.getId(), firstService.getBooking().getId());
        assertEquals(101L, firstService.getService().getId());
        assertEquals(1, firstService.getDayNumber());
        assertEquals(totalCustomers, firstService.getQuantity());
        assertEquals(BookingServiceStatus.CONFIRMED, firstService.getStatus());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[saveTourBookingService] Normal Case: Lịch trình không có dịch vụ nào")
    void saveTourBookingService_whenScheduleHasNoServices_shouldCallSaveAllWithEmptyList() {
        System.out.println("Test Case: Xử lý thành công khi lịch trình không có dịch vụ nào.");
        // Arrange
        TourSchedule schedule = TourSchedule.builder().id(20L).build();
        Booking booking = Booking.builder().id(2L).tourSchedule(schedule).build();
        int totalCustomers = 3;

        // Giả lập repository trả về một danh sách rỗng
        when(partnerServiceRepository.findServicesWithDayNumberByScheduleId(20L)).thenReturn(Collections.emptyList());

        ArgumentCaptor<List<BookingService>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        tourBookingService.saveTourBookingService(booking, totalCustomers);

        // Assert & Verify
        // Xác minh rằng hàm saveAll vẫn được gọi, nhưng với một danh sách rỗng
        verify(bookingServiceRepository, times(1)).saveAll(captor.capture());
        assertTrue(captor.getValue().isEmpty(), "Danh sách dịch vụ được lưu phải là rỗng.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[saveTourBookingService] Abnormal Case: Tham số Booking là null")
    void saveTourBookingService_whenBookingIsNull_shouldThrowException() {
        System.out.println("Test Case: Thất bại khi tham số Booking là null.");
        // Arrange
        Booking nullBooking = null;
        int totalCustomers = 2;

        // Act & Assert
        // Kỳ vọng một NullPointerException vì code sẽ cố gắng gọi nullBooking.getTourSchedule()
        assertThrows(NullPointerException.class, () -> {
            tourBookingService.saveTourBookingService(nullBooking, totalCustomers);
        });

        System.out.println("Log: " + Constants.Message.BOOKING_NOT_FOUND);
        // Xác minh rằng không có tương tác nào với repository
        verify(partnerServiceRepository, never()).findServicesWithDayNumberByScheduleId(anyLong());
        verify(bookingServiceRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("[saveTourBookingService] Abnormal Case: TourSchedule trong Booking là null")
    void saveTourBookingService_whenTourScheduleIsNull_shouldThrowException() {
        System.out.println("Test Case: Thất bại khi TourSchedule trong Booking là null.");
        // Arrange
        Booking bookingWithNullSchedule = Booking.builder().id(3L).tourSchedule(null).build();
        int totalCustomers = 4;

        // Act & Assert
        // Kỳ vọng một NullPointerException vì code sẽ cố gắng gọi tourSchedule.getId()
        assertThrows(NullPointerException.class, () -> {
            tourBookingService.saveTourBookingService(bookingWithNullSchedule, totalCustomers);
        });

        System.out.println("Log: " + Constants.Message.TOUR_SCHEDULE_NOT_FOUND);
        verify(bookingServiceRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("[saveTourBookingService] Abnormal Case: Lỗi khi lưu vào database")
    void saveTourBookingService_whenRepositoryFails_shouldThrowException() {
        System.out.println("Test Case: Thất bại khi có lỗi từ database.");
        // Arrange
        TourSchedule schedule = TourSchedule.builder().id(30L).build();
        Booking booking = Booking.builder().id(4L).tourSchedule(schedule).build();
        int totalCustomers = 1;

        // Giả lập repository tìm thấy dịch vụ
        PartnerServiceWithDayDTO dto = mock(PartnerServiceWithDayDTO.class);
        when(dto.getServiceId()).thenReturn(201L);
        when(dto.getDayNumber()).thenReturn(1);
        List<PartnerServiceWithDayDTO> serviceDTOs = List.of(dto);
        when(partnerServiceRepository.findServicesWithDayNumberByScheduleId(30L)).thenReturn(serviceDTOs);

        // Giả lập hàm saveAll ném ra lỗi
        doThrow(new RuntimeException("Database connection failed")).when(bookingServiceRepository).saveAll(any());

        // Act & Assert
        // Kỳ vọng exception từ repository sẽ được ném ra ngoài
        assertThrows(RuntimeException.class, () -> {
            tourBookingService.saveTourBookingService(booking, totalCustomers);
        });

        System.out.println("Log: " + Constants.Message.GENERAL_FAIL_MESSAGE);
        // Xác minh rằng hàm saveAll đã được gọi, mặc dù nó ném ra lỗi
        verify(bookingServiceRepository, times(1)).saveAll(any());
    }
    @Test
    @DisplayName("[saveTourBookingService] Normal Case: Xử lý thành công khi tổng số khách hàng là 0")
    void saveTourBookingService_whenTotalCustomersIsZero_shouldSaveWithZeroQuantity() {
        System.out.println("Test Case: Xử lý thành công khi tổng số khách hàng là 0.");
        // Arrange
        // 1. Dữ liệu đầu vào
        TourSchedule schedule = TourSchedule.builder().id(40L).build();
        Booking booking = Booking.builder().id(5L).tourSchedule(schedule).build();
        int totalCustomers = 0; // Trường hợp cần test

        // 2. Giả lập repository trả về một dịch vụ
        PartnerServiceWithDayDTO dto = mock(PartnerServiceWithDayDTO.class);
        when(dto.getServiceId()).thenReturn(301L);
        when(dto.getDayNumber()).thenReturn(1);
        List<PartnerServiceWithDayDTO> serviceDTOs = List.of(dto);
        when(partnerServiceRepository.findServicesWithDayNumberByScheduleId(40L)).thenReturn(serviceDTOs);

        // 3. Sử dụng ArgumentCaptor để "bắt" lại danh sách được lưu
        ArgumentCaptor<List<BookingService>> captor = ArgumentCaptor.forClass(List.class);

        // Act
        tourBookingService.saveTourBookingService(booking, totalCustomers);

        // Assert & Verify
        // 4. Xác minh rằng hàm saveAll đã được gọi
        verify(bookingServiceRepository, times(1)).saveAll(captor.capture());

        // 5. Kiểm tra nội dung của danh sách đã được "bắt"
        List<BookingService> savedServices = captor.getValue();
        assertFalse(savedServices.isEmpty(), "Phải có dịch vụ được tạo để lưu.");

        BookingService savedService = savedServices.get(0);
        assertEquals(0, savedService.getQuantity(), "Số lượng (quantity) của dịch vụ phải là 0.");
        assertEquals(booking.getId(), savedService.getBooking().getId());
        assertEquals(301L, savedService.getService().getId());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }
    // =================================================================
    // Test Cases for createReceiptBookingBill
    // =================================================================

    @Test
    @DisplayName("[createReceiptBookingBill] Normal Case: Tạo hóa đơn thành công với đầy đủ thông tin hợp lệ")
    void createReceiptBookingBill_whenAllInputsAreValid_shouldSaveBillAndItem() {
        System.out.println("Test Case: Tạo hóa đơn thành công với đầy đủ thông tin hợp lệ.");
        // Arrange
        User user = User.builder().id(100L).build();
        Booking booking = Booking.builder().id(1L).bookingCode("BK-001").user(user).build();
        Double total = 5000000.0;
        String fullName = "Nguyễn Văn A";
        PaymentMethod paymentMethod = PaymentMethod.BANKING;

        // Giả lập hàm save của repository trả về chính đối tượng được truyền vào
        when(paymentBillRepository.save(any(PaymentBill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentBillItemRepository.save(any(PaymentBillItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Sử dụng ArgumentCaptor để "bắt" lại các đối tượng được lưu
        ArgumentCaptor<PaymentBill> billCaptor = ArgumentCaptor.forClass(PaymentBill.class);
        ArgumentCaptor<PaymentBillItem> itemCaptor = ArgumentCaptor.forClass(PaymentBillItem.class);

        // Act
        tourBookingService.createReceiptBookingBill(booking, total, fullName, paymentMethod);

        // Assert & Verify
        // Xác minh rằng các hàm save đã được gọi
        verify(paymentBillRepository, times(1)).save(billCaptor.capture());
        verify(paymentBillItemRepository, times(1)).save(itemCaptor.capture());

        // Kiểm tra nội dung của PaymentBill đã được "bắt"
        PaymentBill capturedBill = billCaptor.getValue();
        assertEquals(booking.getBookingCode(), capturedBill.getBookingCode());
        assertEquals(fullName, capturedBill.getPaidBy());
        assertEquals(user.getId(), capturedBill.getCreator().getId());
        assertEquals(total, capturedBill.getTotalAmount().doubleValue());
        assertEquals(paymentMethod, capturedBill.getPaymentMethod());

        // Kiểm tra nội dung của PaymentBillItem đã được "bắt"
        PaymentBillItem capturedItem = itemCaptor.getValue();
        assertEquals(total, capturedItem.getAmount().doubleValue());
        assertEquals(PaymentBillItemStatus.PENDING, capturedItem.getPaymentBillItemStatus());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[createReceiptBookingBill] Normal Case: Xử lý thành công khi fullName là null")
    void createReceiptBookingBill_whenFullNameIsNull_shouldSucceed() {
        System.out.println("Test Case: Xử lý thành công khi fullName là null.");
        // Arrange
        User user = User.builder().id(100L).build();
        Booking booking = Booking.builder().id(1L).bookingCode("BK-002").user(user).build();
        Double total = 2500.0;
        String nullFullName = null; // Trường hợp cần test
        PaymentMethod paymentMethod = PaymentMethod.CASH;

        when(paymentBillRepository.save(any(PaymentBill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<PaymentBill> billCaptor = ArgumentCaptor.forClass(PaymentBill.class);

        // Act
        tourBookingService.createReceiptBookingBill(booking, total, nullFullName, paymentMethod);

        // Assert & Verify
        verify(paymentBillRepository, times(1)).save(billCaptor.capture());
        PaymentBill capturedBill = billCaptor.getValue();
        assertNull(capturedBill.getPaidBy(), "Trường paidBy phải là null.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[createReceiptBookingBill] Normal Case: Xử lý thành công khi paymentMethod là null")
    void createReceiptBookingBill_whenPaymentMethodIsNull_shouldSucceed() {
        System.out.println("Test Case: Xử lý thành công khi paymentMethod là null.");
        // Arrange
        User user = User.builder().id(100L).build();
        Booking booking = Booking.builder().id(1L).bookingCode("BK-003").user(user).build();
        Double total = 3000.0;
        String fullName = "Trần Thị B";
        PaymentMethod nullPaymentMethod = null; // Trường hợp cần test

        when(paymentBillRepository.save(any(PaymentBill.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<PaymentBill> billCaptor = ArgumentCaptor.forClass(PaymentBill.class);

        // Act
        tourBookingService.createReceiptBookingBill(booking, total, fullName, nullPaymentMethod);

        // Assert & Verify
        verify(paymentBillRepository, times(1)).save(billCaptor.capture());
        PaymentBill capturedBill = billCaptor.getValue();
        assertNull(capturedBill.getPaymentMethod(), "Trường paymentMethod phải là null.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[createReceiptBookingBill] Abnormal Case: Thất bại khi tham số Booking là null")
    void createReceiptBookingBill_whenBookingIsNull_shouldThrowException() {
        System.out.println("Test Case: Thất bại khi tham số Booking là null.");
        // Arrange
        Booking nullBooking = null; // Trường hợp cần test

        // Act & Assert
        // Kỳ vọng một NullPointerException vì code sẽ cố gắng gọi nullBooking.getBookingCode()
        assertThrows(NullPointerException.class, () -> {
            tourBookingService.createReceiptBookingBill(nullBooking, 1000.0, "Test", PaymentMethod.BANKING);
        });

        System.out.println("Log: " + Constants.Message.BOOKING_NOT_FOUND);
        // Xác minh rằng không có tương tác nào với repository
        verify(paymentBillRepository, never()).save(any());
        verify(paymentBillItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("[createReceiptBookingBill] Abnormal Case: Thất bại khi User trong Booking là null")
    void createReceiptBookingBill_whenUserInBookingIsNull_shouldThrowException() {
        System.out.println("Test Case: Thất bại khi User trong Booking là null.");
        // Arrange
        // Tạo booking hợp lệ nhưng không có user
        Booking bookingWithNullUser = Booking.builder().id(2L).bookingCode("BK-004").user(null).build();

        // Act & Assert
        // Kỳ vọng một NullPointerException vì code sẽ cố gắng gọi tourBooking.getUser().getId()
        assertThrows(NullPointerException.class, () -> {
            tourBookingService.createReceiptBookingBill(bookingWithNullUser, 1000.0, "Test", PaymentMethod.BANKING);
        });

        System.out.println("Log: " + Constants.Message.USER_INFO_NOT_FOUND);
        verify(paymentBillRepository, never()).save(any());
        verify(paymentBillItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("[createReceiptBookingBill] Abnormal Case: Thất bại khi tham số total là null")
    void createReceiptBookingBill_whenTotalIsNull_shouldThrowException() {
        System.out.println("Test Case: Thất bại khi tham số total là null.");
        // Arrange
        User user = User.builder().id(100L).build();
        Booking booking = Booking.builder().id(1L).bookingCode("BK-005").user(user).build();
        Double nullTotal = null; // Trường hợp cần test

        // Act & Assert
        // Kỳ vọng một NullPointerException vì code sẽ gọi BigDecimal.valueOf(null)
        assertThrows(NullPointerException.class, () -> {
            tourBookingService.createReceiptBookingBill(booking, nullTotal, "Test", PaymentMethod.BANKING);
        });

        System.out.println("Log: " + Constants.Message.INVALID_PRICE);
        verify(paymentBillRepository, never()).save(any());
        verify(paymentBillItemRepository, never()).save(any());
    }

}