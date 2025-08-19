//package com.fpt.capstone.tourism.service.impl;
//
//import com.fpt.capstone.tourism.constants.Constants;
//import com.fpt.capstone.tourism.dto.general.GeneralResponse;
//import com.fpt.capstone.tourism.dto.general.PagingDTO;
//import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
//import com.fpt.capstone.tourism.model.User;
//import com.fpt.capstone.tourism.model.enums.BookingStatus;
//import com.fpt.capstone.tourism.model.tour.Booking;
//import com.fpt.capstone.tourism.model.tour.Tour;
//import com.fpt.capstone.tourism.model.tour.TourSchedule;
//import com.fpt.capstone.tourism.repository.booking.BookingRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//import org.springframework.http.HttpStatus;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SellerBookingServiceImplTest {
//
//    @InjectMocks
//    private SellerBookingServiceImpl sellerBookingService;
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    private Booking mockBooking1;
//    private Booking mockBooking2;
//
//    @BeforeEach
//    void setUp() {
//        // --- Dữ liệu giả lập ---
//        // Để toSummaryDTO() hoạt động, chúng ta cần tạo một cấu trúc đối tượng lồng nhau
//        User customer = User.builder().id(100L).fullName("Nguyễn Văn Khách").build();
//        Tour tour = Tour.builder().id(1L).name("Tour Đà Nẵng 3N2Đ").build();
//        TourSchedule schedule = TourSchedule.builder().id(10L).tour(tour).departureDate(LocalDateTime.now().plusDays(10)).build();
//
//        // SỬA LỖI: Tạo đối tượng trước, sau đó dùng setter cho các trường kế thừa
//        mockBooking1 = Booking.builder()
//                .id(1L)
//                .bookingCode("BK-001")
//                .tourSchedule(schedule)
//                .user(customer)
//                .adults(2)
//                .children(1)
//                .bookingStatus(BookingStatus.PENDING)
//                .seller(null) // Quan trọng: seller là null
//                .build();
//        mockBooking1.setCreatedAt(LocalDateTime.now().minusDays(1)); // Gán giá trị cho trường kế thừa
//
//        mockBooking2 = Booking.builder()
//                .id(2L)
//                .bookingCode("BK-002")
//                .tourSchedule(schedule)
//                .user(customer)
//                .adults(1)
//                .children(0)
//                .bookingStatus(BookingStatus.PENDING)
//                .seller(null) // Quan trọng: seller là null
//                .build();
//        mockBooking2.setCreatedAt(LocalDateTime.now()); // Gán giá trị cho trường kế thừa
//    }
//
//    // =================================================================
//    // 1. Test Cases for getAvailableBookings
//    // =================================================================
//
//    @Test
//    @DisplayName("[getAvailableBookings] Normal Case: Lấy danh sách booking khả dụng thành công")
//    void getAvailableBookings_whenBookingsExist_shouldReturnPagedResult() {
//        System.out.println("Test Case: Normal Input - Lấy danh sách booking khả dụng thành công.");
//        // Arrange
//        int page = 0;
//        int size = 10;
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//        // Do mockBooking2 được tạo sau nên nó sẽ đứng đầu khi sắp xếp giảm dần
//        List<Booking> bookingList = List.of(mockBooking2, mockBooking1);
//        Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, bookingList.size());
//
//        // Giả lập repository trả về một trang có dữ liệu
//        when(bookingRepository.findBySellerIsNull(any(Pageable.class))).thenReturn(bookingPage);
//
//        // Act
//        GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> response = sellerBookingService.getAvailableBookings(page, size);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK.value(), response.getCode());
//        assertEquals("Success", response.getMessage());
//
//        PagingDTO<SellerBookingSummaryDTO> pagingDTO = response.getData();
//        assertNotNull(pagingDTO);
//        assertEquals(bookingPage.getTotalElements(), pagingDTO.getTotal());
//        assertEquals(bookingPage.getNumber(), pagingDTO.getPage());
//        assertEquals(2, pagingDTO.getItems().size());
//
//        // Sửa lỗi logic test: booking2 mới hơn booking1 nên sẽ ở vị trí đầu tiên
//        assertEquals("BK-002", pagingDTO.getItems().get(0).getBookingCode(), "Phải sắp xếp theo ngày tạo mới nhất (booking 2)");
//        // Sửa lỗi logic test: Số ghế của booking 2 là 1 (1 adult + 0 children)
//        assertEquals(1, pagingDTO.getItems().get(0).getSeats(), "Số ghế của booking 2 phải là 1.");
//
//        // Verify
//        verify(bookingRepository, times(1)).findBySellerIsNull(any(Pageable.class));
//        System.out.println("Log: " + Constants.Message.SUCCESS);
//    }
//
//    @Test
//    @DisplayName("[getAvailableBookings] Normal Case: Trả về danh sách rỗng khi không có booking nào")
//    void getAvailableBookings_whenNoBookingsExist_shouldReturnEmptyPage() {
//        System.out.println("Test Case: Normal Input - Trả về danh sách rỗng khi không có booking nào.");
//        // Arrange
//        int page = 0;
//        int size = 10;
//
//        // Giả lập repository trả về một trang rỗng
//        when(bookingRepository.findBySellerIsNull(any(Pageable.class))).thenReturn(Page.empty());
//
//        // Act
//        GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> response = sellerBookingService.getAvailableBookings(page, size);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK.value(), response.getCode());
//
//        PagingDTO<SellerBookingSummaryDTO> pagingDTO = response.getData();
//        assertNotNull(pagingDTO);
//        assertEquals(0, pagingDTO.getTotal());
//        assertTrue(pagingDTO.getItems().isEmpty());
//
//        // Verify
//        verify(bookingRepository, times(1)).findBySellerIsNull(any(Pageable.class));
//        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + Constants.Message.NO_SERVICES_AVAILABLE);
//    }
//
//    @Test
//    @DisplayName("[getAvailableBookings] Abnormal Case: Thất bại khi page là số âm")
//    void getAvailableBookings_whenPageIsNegative_shouldThrowException() {
//        System.out.println("Test Case: Abnormal Input - Thất bại khi page là số âm.");
//        // Arrange
//        int invalidPage = -1;
//        int size = 10;
//
//        // Act & Assert
//        // PageRequest.of() sẽ ném ra lỗi này trước khi gọi repository
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            sellerBookingService.getAvailableBookings(invalidPage, size);
//        });
//
//        assertTrue(exception.getMessage().contains("Page index must not be less than zero"));
//
//        // Verify
//        verify(bookingRepository, never()).findBySellerIsNull(any());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Page không được là số âm.");
//    }
//
//    @Test
//    @DisplayName("[getAvailableBookings] Abnormal Case: Thất bại khi size nhỏ hơn 1")
//    void getAvailableBookings_whenSizeIsLessThanOne_shouldThrowException() {
//        System.out.println("Test Case: Abnormal Input - Thất bại khi size nhỏ hơn 1.");
//        // Arrange
//        int page = 0;
//        int invalidSize = 0;
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
//            sellerBookingService.getAvailableBookings(page, invalidSize);
//        });
//
//        assertTrue(exception.getMessage().contains("Page size must not be less than one"));
//
//        // Verify
//        verify(bookingRepository, never()).findBySellerIsNull(any());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Size phải lớn hơn 0.");
//    }
//
//    @Test
//    @DisplayName("[getAvailableBookings] Abnormal Case: Thất bại khi repository ném ra lỗi")
//    void getAvailableBookings_whenRepositoryThrowsException_shouldPropagateException() {
//        System.out.println("Test Case: Abnormal Input - Thất bại khi repository ném ra lỗi.");
//        // Arrange
//        int page = 0;
//        int size = 10;
//
//        // Giả lập repository ném ra một lỗi runtime
//        when(bookingRepository.findBySellerIsNull(any(Pageable.class)))
//                .thenThrow(new RuntimeException("Database connection error"));
//
//        // Act & Assert
//        // Kỳ vọng một RuntimeException sẽ được ném ra từ service vì không có khối try-catch
//        assertThrows(RuntimeException.class, () -> {
//            sellerBookingService.getAvailableBookings(page, size);
//        });
//
//        // Verify
//        verify(bookingRepository, times(1)).findBySellerIsNull(any(Pageable.class));
//        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
//    }
//}