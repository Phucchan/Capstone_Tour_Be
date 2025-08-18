package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyNewUserDTO;
import com.fpt.capstone.tourism.dto.response.analytic.MonthlyRevenueDTO;
import com.fpt.capstone.tourism.dto.response.analytic.TourRevenueDTO;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
// SỬA LỖI: Thêm dòng import static cho tất cả các phương thức của Mockito
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository; // Mocked because it's a dependency, though not used in this specific method

    // =================================================================
    // Test Cases for getTopToursByRevenue
    // =================================================================

    // --- Valid Input Cases ---

    @Test
    @DisplayName("[getTopToursByRevenue] Valid Input: Lấy top tour thành công với đầy đủ tham số hợp lệ")
    void getTopToursByRevenue_whenAllInputsAreValid_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy top tour thành công với đầy đủ tham số hợp lệ.");
        // Arrange
        int limit = 5;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // SỬA LỖI: Khởi tạo List một cách tường minh để tránh lỗi suy luận kiểu
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1L, "Tour Đà Nẵng", "PUBLIC", 15000000.0});
        mockResults.add(new Object[]{2L, "Tour Hà Nội", "PRIVATE", 12000000.0});

        when(bookingRepository.findTopToursByRevenue(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(mockResults);

        // Act
        GeneralResponse<List<TourRevenueDTO>> response = analyticsService.getTopToursByRevenue(limit, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());

        TourRevenueDTO firstTour = response.getData().get(0);
        assertEquals(1L, firstTour.getId());
        assertEquals("Tour Đà Nẵng", firstTour.getName());
        assertEquals("PUBLIC", firstTour.getTourType());
        assertEquals(15000000.0, firstTour.getTotalRevenue());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getTopToursByRevenue] Valid Input: Lấy top tour thành công khi startDate và endDate là null")
    void getTopToursByRevenue_whenDatesAreNull_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy top tour thành công khi ngày tháng là null.");
        // Arrange
        int limit = 3;

        // SỬA LỖI: Khởi tạo List một cách tường minh
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{1L, "Tour Toàn Quốc", "PUBLIC", 50000000.0});

        // Giả lập repository được gọi với tham số ngày là null
        when(bookingRepository.findTopToursByRevenue(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(mockResults);

        // Act
        GeneralResponse<List<TourRevenueDTO>> response = analyticsService.getTopToursByRevenue(limit, null, null);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(1L, response.getData().get(0).getId());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getTopToursByRevenue] Valid Input: Trả về danh sách rỗng khi không có tour nào")
    void getTopToursByRevenue_whenNoToursFound_shouldReturnEmptyList() {
        System.out.println("Test Case: Valid Input - Trả về danh sách rỗng khi không có tour nào.");
        // Arrange
        int limit = 5;
        // Cung cấp một ArrayList rỗng có kiểu dữ liệu đúng
        when(bookingRepository.findTopToursByRevenue(any(), any(), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<List<TourRevenueDTO>> response = analyticsService.getTopToursByRevenue(limit, LocalDate.now(), LocalDate.now().plusDays(1));

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty(), "Danh sách trả về phải là rỗng.");

        System.out.println("Log: " + Constants.Message.SUCCESS + ". Không có dữ liệu để hiển thị.");
    }

    // --- Invalid Input Cases ---

    @Test
    @DisplayName("[getTopToursByRevenue] Invalid Input: Thất bại khi limit là 0")
    void getTopToursByRevenue_whenLimitIsZero_shouldThrowIllegalArgumentException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi limit là 0.");
        // Arrange
        int invalidLimit = 0;

        // Act & Assert
        // PageRequest.of(0, 0) sẽ ném ra IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            analyticsService.getTopToursByRevenue(invalidLimit, null, null);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Limit phải lớn hơn 0.");
    }

    @Test
    @DisplayName("[getTopToursByRevenue] Invalid Input: Thất bại khi limit là số âm")
    void getTopToursByRevenue_whenLimitIsNegative_shouldThrowIllegalArgumentException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi limit là số âm.");
        // Arrange
        int invalidLimit = -5;

        // Act & Assert
        // PageRequest.of(0, -5) sẽ ném ra IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            analyticsService.getTopToursByRevenue(invalidLimit, null, null);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Limit phải lớn hơn 0.");
    }

    @Test
    @DisplayName("[getTopToursByRevenue] Invalid Input: Xử lý khi ngày bắt đầu sau ngày kết thúc")
    void getTopToursByRevenue_whenStartDateIsAfterEndDate_shouldReturnEmptyList() {
        System.out.println("Test Case: Invalid Input - Xử lý khi ngày bắt đầu sau ngày kết thúc.");
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 10);
        LocalDate endDate = LocalDate.of(2024, 1, 1); // Ngày kết thúc trước ngày bắt đầu

        // Cung cấp một ArrayList rỗng có kiểu dữ liệu đúng
        when(bookingRepository.findTopToursByRevenue(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<List<TourRevenueDTO>> response = analyticsService.getTopToursByRevenue(5, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty(), "Phải trả về danh sách rỗng khi khoảng ngày không hợp lệ.");

        System.out.println("Log: " + Constants.Message.INVALID_DATE_RANGE);
    }

    // =================================================================
    // Test Cases for getMonthlyRevenue
    // =================================================================

    @Test
    @DisplayName("[getMonthlyRevenue] Valid Case: Lấy doanh thu theo tháng cho một tour cụ thể")
    void getMonthlyRevenue_withValidTourId_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy doanh thu theo tháng cho một tour cụ thể.");
        // Arrange
        Long tourId = 1L;
        int year = 2023; // Tham số này hiện không được sử dụng trong service
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Dữ liệu giả lập trả về từ repository
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{2023, 1, 15000000.0});
        mockResults.add(new Object[]{2023, 2, 25000000.0});

        when(bookingRepository.findMonthlyRevenueByTour(eq(tourId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockResults);

        // Act
        GeneralResponse<List<MonthlyRevenueDTO>> response = analyticsService.getMonthlyRevenue(tourId, year, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size(), "Phải trả về 2 bản ghi doanh thu theo tháng.");

        MonthlyRevenueDTO firstMonth = response.getData().get(0);
        assertEquals(2023, firstMonth.getYear());
        assertEquals(1, firstMonth.getMonth());
        assertEquals(15000000.0, firstMonth.getRevenue());

        // Verify
        verify(bookingRepository, times(1)).findMonthlyRevenueByTour(anyLong(), any(), any());
        verify(bookingRepository, never()).findMonthlyRevenueSummary(any(), any()); // Đảm bảo hàm summary không được gọi

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getMonthlyRevenue] Valid Case: Lấy doanh thu tổng hợp khi tourId là null")
    void getMonthlyRevenue_withNullTourId_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy doanh thu tổng hợp khi tourId là null.");
        // Arrange
        Long nullTourId = null; // Trường hợp cần test
        int year = 2023;

        // Dữ liệu giả lập trả về từ repository
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{2023, 10, 100000000.0});
        mockResults.add(new Object[]{2023, 11, 120000000.0});

        when(bookingRepository.findMonthlyRevenueSummary(isNull(), isNull()))
                .thenReturn(mockResults);

        // Act
        GeneralResponse<List<MonthlyRevenueDTO>> response = analyticsService.getMonthlyRevenue(nullTourId, year, null, null);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());

        MonthlyRevenueDTO secondMonth = response.getData().get(1);
        assertEquals(2023, secondMonth.getYear());
        assertEquals(11, secondMonth.getMonth());
        assertEquals(120000000.0, secondMonth.getRevenue());

        // Verify
        verify(bookingRepository, never()).findMonthlyRevenueByTour(any(), any(), any()); // Đảm bảo hàm theo tour không được gọi
        verify(bookingRepository, times(1)).findMonthlyRevenueSummary(isNull(), isNull());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getMonthlyRevenue] Valid Case: Trả về danh sách rỗng khi không có dữ liệu doanh thu")
    void getMonthlyRevenue_whenNoDataFound_shouldReturnEmptyList() {
        System.out.println("Test Case: Valid Input - Trả về danh sách rỗng khi không có dữ liệu.");
        // Arrange
        Long tourId = 99L; // Một tour không có doanh thu
        when(bookingRepository.findMonthlyRevenueByTour(eq(tourId), any(), any()))
                .thenReturn(new ArrayList<>()); // Trả về list rỗng

        // Act
        GeneralResponse<List<MonthlyRevenueDTO>> response = analyticsService.getMonthlyRevenue(tourId, 2024, null, null);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty(), "Danh sách trả về phải là rỗng.");

        System.out.println("Log: " + Constants.Message.SUCCESS + ". Không có dữ liệu để hiển thị.");
    }

    @Test
    @DisplayName("[getMonthlyRevenue] Invalid Case: Xử lý khi ngày bắt đầu sau ngày kết thúc")
    void getMonthlyRevenue_whenStartDateIsAfterEndDate_shouldReturnEmptyList() {
        System.out.println("Test Case: Invalid Input - Xử lý khi ngày bắt đầu sau ngày kết thúc.");
        // Arrange
        Long tourId = 1L;
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31); // Ngày kết thúc trước ngày bắt đầu

        // Giả lập rằng khi khoảng ngày không hợp lệ, repository sẽ trả về danh sách rỗng
        when(bookingRepository.findMonthlyRevenueByTour(eq(tourId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<List<MonthlyRevenueDTO>> response = analyticsService.getMonthlyRevenue(tourId, 2024, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty(), "Phải trả về danh sách rỗng khi khoảng ngày không hợp lệ.");

        System.out.println("Log: " + Constants.Message.INVALID_DATE_RANGE);
    }

    @Test
    @DisplayName("[getMonthlyRevenue] Edge Case: Xử lý khi tham số 'year' không hợp lệ (nhưng không ảnh hưởng)")
    void getMonthlyRevenue_withInvalidYear_shouldStillSucceed() {
        System.out.println("Test Case: Edge Case - Xử lý khi tham số 'year' không hợp lệ.");
        // Arrange
        Long nullTourId = null;
        int invalidYear = -1; // Tham số này không được sử dụng trong logic của service

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{2023, 12, 9999.0});
        when(bookingRepository.findMonthlyRevenueSummary(isNull(), isNull()))
                .thenReturn(mockResults);

        // Act & Assert
        // Hàm vẫn sẽ chạy thành công vì 'year' không được dùng
        assertDoesNotThrow(() -> {
            GeneralResponse<List<MonthlyRevenueDTO>> response = analyticsService.getMonthlyRevenue(nullTourId, invalidYear, null, null);
            assertNotNull(response);
            assertFalse(response.getData().isEmpty());
        });

        System.out.println("Log: " + Constants.Message.SUCCESS + ". (Lưu ý: tham số 'year' hiện không được sử dụng).");
    }
    // =================================================================
    // Test Cases for getMonthlyNewUsers
    // =================================================================

    @Test
    @DisplayName("[getMonthlyNewUsers] Valid Case: Lấy số lượng người dùng mới thành công với đầy đủ tham số")
    void getMonthlyNewUsers_whenAllInputsAreValid_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy số lượng người dùng mới thành công với đầy đủ tham số.");
        // Arrange
        int year = 2023;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        // Dữ liệu giả lập trả về từ repository
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{2023, 1, 50L});
        mockResults.add(new Object[]{2023, 2, 75L});

        when(userRepository.countNewUsersByMonth(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockResults);

        // Act
        GeneralResponse<List<MonthlyNewUserDTO>> response = analyticsService.getMonthlyNewUsers(year, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size(), "Phải trả về 2 bản ghi số lượng người dùng mới.");

        MonthlyNewUserDTO firstMonth = response.getData().get(0);
        assertEquals(2023, firstMonth.getYear());
        assertEquals(1, firstMonth.getMonth());
        assertEquals(50L, firstMonth.getUserCount());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getMonthlyNewUsers] Valid Case: Lấy số lượng người dùng mới thành công khi ngày tháng là null")
    void getMonthlyNewUsers_whenDatesAreNull_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy số lượng người dùng mới thành công khi ngày tháng là null.");
        // Arrange
        int year = 2024;

        // Dữ liệu giả lập
        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{2024, 5, 120L});

        when(userRepository.countNewUsersByMonth(isNull(), isNull()))
                .thenReturn(mockResults);

        // Act
        GeneralResponse<List<MonthlyNewUserDTO>> response = analyticsService.getMonthlyNewUsers(year, null, null);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertEquals(120L, response.getData().get(0).getUserCount());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getMonthlyNewUsers] Valid Case: Trả về danh sách rỗng khi không có người dùng mới")
    void getMonthlyNewUsers_whenNoNewUsersFound_shouldReturnEmptyList() {
        System.out.println("Test Case: Valid Input - Trả về danh sách rỗng khi không có người dùng mới.");
        // Arrange
        int year = 2024;
        // Giả lập repository trả về danh sách rỗng
        when(userRepository.countNewUsersByMonth(any(), any()))
                .thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<List<MonthlyNewUserDTO>> response = analyticsService.getMonthlyNewUsers(year, LocalDate.now(), LocalDate.now().plusDays(1));

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty(), "Danh sách trả về phải là rỗng.");

        System.out.println("Log: " + Constants.Message.SUCCESS + ". Không có dữ liệu để hiển thị.");
    }

    @Test
    @DisplayName("[getMonthlyNewUsers] Invalid Case: Xử lý khi ngày bắt đầu sau ngày kết thúc")
    void getMonthlyNewUsers_whenStartDateIsAfterEndDate_shouldReturnEmptyList() {
        System.out.println("Test Case: Invalid Input - Xử lý khi ngày bắt đầu sau ngày kết thúc.");
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31); // Ngày kết thúc trước ngày bắt đầu

        // Giả lập rằng khi khoảng ngày không hợp lệ, repository sẽ trả về danh sách rỗng
        when(userRepository.countNewUsersByMonth(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Act
        GeneralResponse<List<MonthlyNewUserDTO>> response = analyticsService.getMonthlyNewUsers(2024, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getData());
        assertTrue(response.getData().isEmpty(), "Phải trả về danh sách rỗng khi khoảng ngày không hợp lệ.");

        System.out.println("Log: " + Constants.Message.INVALID_DATE_RANGE);
    }

    @Test
    @DisplayName("[getMonthlyNewUsers] Edge Case: Xử lý khi tham số 'year' không hợp lệ (nhưng không ảnh hưởng)")
    void getMonthlyNewUsers_withInvalidYear_shouldStillSucceed() {
        System.out.println("Test Case: Edge Case - Xử lý khi tham số 'year' không hợp lệ.");
        // Arrange
        int invalidYear = 0; // Tham số này không được sử dụng trong logic của service

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{2023, 12, 99L});
        when(userRepository.countNewUsersByMonth(isNull(), isNull()))
                .thenReturn(mockResults);

        // Act & Assert
        // Hàm vẫn sẽ chạy thành công vì 'year' không được dùng
        assertDoesNotThrow(() -> {
            GeneralResponse<List<MonthlyNewUserDTO>> response = analyticsService.getMonthlyNewUsers(invalidYear, null, null);
            assertNotNull(response);
            assertFalse(response.getData().isEmpty());
            assertEquals(99L, response.getData().get(0).getUserCount());
        });

        System.out.println("Log: " + Constants.Message.SUCCESS + ". (Lưu ý: tham số 'year' hiện không được sử dụng).");
    }
    // =================================================================
    // Test Cases for getTotalRevenue
    // =================================================================

    @Test
    @DisplayName("[getTotalRevenue] Normal Case: Lấy tổng doanh thu thành công với khoảng ngày hợp lệ")
    void getTotalRevenue_whenDateRangeIsValid_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy tổng doanh thu thành công với khoảng ngày hợp lệ.");
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        Double expectedRevenue = 123456789.0;

        // Giả lập repository trả về một giá trị doanh thu
        when(bookingRepository.calculateTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedRevenue);

        // Act
        GeneralResponse<Double> response = analyticsService.getTotalRevenue(startDate, endDate);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(expectedRevenue, response.getData(), "Doanh thu trả về phải khớp với giá trị giả lập.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getTotalRevenue] Normal Case: Lấy tổng doanh thu thành công khi không có ngày tháng (tất cả thời gian)")
    void getTotalRevenue_whenDatesAreNull_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy tổng doanh thu thành công khi ngày tháng là null.");
        // Arrange
        Double expectedTotalRevenue = 999999999.0;

        // Giả lập repository được gọi với tham số null và trả về tổng doanh thu
        when(bookingRepository.calculateTotalRevenue(isNull(), isNull()))
                .thenReturn(expectedTotalRevenue);

        // Act
        GeneralResponse<Double> response = analyticsService.getTotalRevenue(null, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals(expectedTotalRevenue, response.getData());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getTotalRevenue] Normal Case: Xử lý khi không có doanh thu nào (repository trả về null)")
    void getTotalRevenue_whenNoRevenueFound_shouldReturnNullData() {
        System.out.println("Test Case: Normal Input - Xử lý khi không có doanh thu (repository trả về null).");
        // Arrange
        // Giả lập repository không tìm thấy doanh thu và trả về null
        when(bookingRepository.calculateTotalRevenue(any(), any())).thenReturn(null);

        // Act
        GeneralResponse<Double> response = analyticsService.getTotalRevenue(LocalDate.now(), LocalDate.now());

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertNull(response.getData(), "Dữ liệu doanh thu trả về phải là null khi không có doanh thu.");

        System.out.println("Log: " + Constants.Message.SUCCESS + ". Không có dữ liệu để hiển thị.");
    }

    @Test
    @DisplayName("[getTotalRevenue] Abnormal Case: Xử lý khi ngày bắt đầu sau ngày kết thúc")
    void getTotalRevenue_whenStartDateIsAfterEndDate_shouldReturnZeroOrNull() {
        System.out.println("Test Case: Abnormal Input - Xử lý khi ngày bắt đầu sau ngày kết thúc.");
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 2, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31); // Ngày kết thúc trước ngày bắt đầu
        Double expectedRevenue = 0.0; // Hoặc null, tùy vào logic của DB. Giả sử là 0.0

        // Giả lập rằng khi khoảng ngày không hợp lệ, repository sẽ trả về 0
        when(bookingRepository.calculateTotalRevenue(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedRevenue);

        // Act
        GeneralResponse<Double> response = analyticsService.getTotalRevenue(startDate, endDate);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals(expectedRevenue, response.getData(), "Phải trả về 0.0 khi khoảng ngày không hợp lệ.");

        System.out.println("Log: " + Constants.Message.INVALID_DATE_RANGE);
    }

    @Test
    @DisplayName("[getTotalRevenue] Abnormal Case: Thất bại khi repository ném ra lỗi")
    void getTotalRevenue_whenRepositoryThrowsException_shouldPropagateException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        // Giả lập repository ném ra một lỗi runtime khi được gọi
        when(bookingRepository.calculateTotalRevenue(any(), any()))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        // Kỳ vọng một RuntimeException sẽ được ném ra từ service
        // vì không có khối try-catch trong hàm getTotalRevenue
        assertThrows(RuntimeException.class, () -> {
            analyticsService.getTotalRevenue(LocalDate.now(), LocalDate.now());
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Lỗi từ tầng repository.");
    }
}