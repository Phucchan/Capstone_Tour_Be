//package com.fpt.capstone.tourism.service.impl;
//
//import com.fpt.capstone.tourism.constants.Constants;
//import com.fpt.capstone.tourism.dto.common.TourDiscountDTO;
//import com.fpt.capstone.tourism.dto.general.GeneralResponse;
//import com.fpt.capstone.tourism.dto.general.PagingDTO;
//import com.fpt.capstone.tourism.dto.request.TourDiscountRequestDTO;
//import com.fpt.capstone.tourism.dto.response.TourDiscountSummaryDTO;
//import com.fpt.capstone.tourism.exception.common.BusinessException;
//import com.fpt.capstone.tourism.mapper.TourDiscountMapper;
//import com.fpt.capstone.tourism.model.tour.TourDiscount;
//import com.fpt.capstone.tourism.model.tour.TourSchedule;
//import com.fpt.capstone.tourism.repository.tour.TourDiscountRepository;
//import com.fpt.capstone.tourism.repository.tour.TourScheduleRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TourDiscountServiceImplTest {
//
//    @InjectMocks
//    private TourDiscountServiceImpl tourDiscountService;
//
//    @Mock
//    private TourDiscountRepository tourDiscountRepository;
//    @Mock
//    private TourScheduleRepository tourScheduleRepository;
//    @Mock
//    private TourDiscountMapper tourDiscountMapper;
//
//    private TourDiscountRequestDTO.TourDiscountRequestDTOBuilder validRequestBuilder;
//    private TourSchedule mockTourSchedule;
//
//    @BeforeEach
//    void setUp() {
//        // Khởi tạo một builder cho request hợp lệ để tái sử dụng và chỉnh sửa trong các bài test
//        validRequestBuilder = TourDiscountRequestDTO.builder()
//                .scheduleId(1L)
//                .discountPercent(15.0)
//                .startDate(LocalDateTime.now().plusDays(1))
//                .endDate(LocalDateTime.now().plusDays(10));
//
//        mockTourSchedule = TourSchedule.builder().id(1L).build();
//    }
//
//    // =================================================================
//    // 1. Valid Input Case
//    // =================================================================
//
//    @Test
//    @DisplayName("[createDiscount] Valid Input: Tạo giảm giá thành công với dữ liệu hợp lệ")
//    void createDiscount_whenRequestIsValid_shouldSucceed() {
//        System.out.println("Test Case: Valid Input - Tạo giảm giá thành công với dữ liệu hợp lệ.");
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder.build();
//
//        when(tourScheduleRepository.findById(requestDTO.getScheduleId())).thenReturn(Optional.of(mockTourSchedule));
//        when(tourDiscountRepository.findFirstByTourSchedule_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
//                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
//        )).thenReturn(Optional.empty());
//
//        TourDiscount savedDiscount = TourDiscount.builder().id(100L).discountPercent(15.0).build();
//        when(tourDiscountRepository.save(any(TourDiscount.class))).thenReturn(savedDiscount);
//        when(tourDiscountMapper.toDTO(savedDiscount)).thenReturn(TourDiscountDTO.builder().id(100L).discountPercent(15.0).build());
//
//        // Act
//        GeneralResponse<TourDiscountDTO> response = tourDiscountService.createDiscount(requestDTO);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK.value(), response.getStatus());
//        assertEquals(Constants.Message.TOUR_DISCOUNT_CREATE_SUCCESS, response.getMessage());
//        assertNotNull(response.getData());
//        assertEquals(15.0, response.getData().getDiscountPercent());
//
//        // Verify
//        ArgumentCaptor<TourDiscount> captor = ArgumentCaptor.forClass(TourDiscount.class);
//        verify(tourDiscountRepository, times(1)).save(captor.capture());
//        TourDiscount capturedDiscount = captor.getValue();
//        assertEquals(mockTourSchedule.getId(), capturedDiscount.getTourSchedule().getId());
//        assertFalse(capturedDiscount.getDeleted());
//
//        System.out.println("Log: " + Constants.Message.SUCCESS);
//    }
//
//    // =================================================================
//    // 2. Invalid Input Cases
//    // =================================================================
//
//    @ParameterizedTest(name = "Thất bại khi phần trăm giảm giá là {0}")
//    @ValueSource(doubles = {0.0, -10.0, 100.1})
//    @DisplayName("[createDiscount] Invalid Input: Thất bại khi phần trăm giảm giá không hợp lệ")
//    void createDiscount_whenDiscountPercentIsInvalid_shouldThrowBusinessException(double invalidPercent) {
//        System.out.println("Test Case: Invalid Input - Thất bại khi phần trăm giảm giá là " + invalidPercent);
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder.discountPercent(invalidPercent).build();
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.createDiscount(requestDTO);
//        });
//
//        assertEquals(Constants.Message.TOUR_DISCOUNT_INVALID_PERCENT, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_DISCOUNT_INVALID_PERCENT);
//        verify(tourDiscountRepository, never()).save(any());
//    }
//
//    @Test
//    @DisplayName("[createDiscount] Invalid Input: Thất bại khi ngày bắt đầu là ngày trong quá khứ")
//    void createDiscount_whenStartDateIsInThePast_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi ngày bắt đầu là ngày trong quá khứ.");
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder.startDate(LocalDateTime.now().minusMinutes(1)).build();
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.createDiscount(requestDTO);
//        });
//
//        assertEquals(Constants.Message.TOUR_DISCOUNT_START_DATE_IN_PAST, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_DISCOUNT_START_DATE_IN_PAST);
//    }
//
//    @Test
//    @DisplayName("[createDiscount] Invalid Input: Thất bại khi ngày bắt đầu sau ngày kết thúc")
//    void createDiscount_whenStartDateIsAfterEndDate_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi ngày bắt đầu sau ngày kết thúc.");
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder
//                .startDate(LocalDateTime.now().plusDays(10))
//                .endDate(LocalDateTime.now().plusDays(1))
//                .build();
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.createDiscount(requestDTO);
//        });
//
//        assertEquals(Constants.Message.TOUR_DISCOUNT_INVALID_DATE_RANGE, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_DISCOUNT_INVALID_DATE_RANGE);
//    }
//
//    @Test
//    @DisplayName("[createDiscount] Invalid Input: Thất bại khi không tìm thấy TourSchedule")
//    void createDiscount_whenTourScheduleNotFound_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi không tìm thấy TourSchedule.");
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder.scheduleId(99L).build();
//        when(tourScheduleRepository.findById(99L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.createDiscount(requestDTO);
//        });
//
//        assertEquals(Constants.Message.TOUR_SCHEDULE_NOT_FOUND, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_SCHEDULE_NOT_FOUND);
//    }
//
//    @Test
//    @DisplayName("[createDiscount] Invalid Input: Thất bại khi giảm giá cho lịch trình đã tồn tại trong khoảng thời gian")
//    void createDiscount_whenDiscountOverlaps_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi giảm giá cho lịch trình đã tồn tại trong khoảng thời gian.");
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder.build();
//        when(tourScheduleRepository.findById(requestDTO.getScheduleId())).thenReturn(Optional.of(mockTourSchedule));
//        when(tourDiscountRepository.findFirstByTourSchedule_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
//                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
//        )).thenReturn(Optional.of(new TourDiscount())); // Giả lập tìm thấy discount trùng lặp
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.createDiscount(requestDTO);
//        });
//
//        assertEquals(Constants.Message.TOUR_DISCOUNT_EXISTS, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_DISCOUNT_EXISTS);
//    }
//
//    @Test
//    @DisplayName("[createDiscount] Invalid Input: Thất bại khi scheduleId là null")
//    void createDiscount_whenScheduleIdIsNull_shouldThrowException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi scheduleId là null.");
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder.scheduleId(null).build();
//
//        // Giả lập findById ném ra lỗi khi nhận vào null, đây là hành vi mặc định của Spring Data JPA.
//        when(tourScheduleRepository.findById(null)).thenThrow(new IllegalArgumentException("ID must not be null"));
//
//        // Act & Assert
//        // Service sẽ bắt lỗi này và gói nó trong một BusinessException
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.createDiscount(requestDTO);
//        });
//
//        assertEquals(Constants.Message.TOUR_DISCOUNT_CREATE_FAIL, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: ID không được là null.");
//    }
//
//    @Test
//    @DisplayName("[createDiscount] Invalid Input: Thất bại khi repository ném ra lỗi lúc lưu")
//    void createDiscount_whenRepositoryFailsOnSave_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi repository ném ra lỗi lúc lưu.");
//        // Arrange
//        TourDiscountRequestDTO requestDTO = validRequestBuilder.build();
//        when(tourScheduleRepository.findById(requestDTO.getScheduleId())).thenReturn(Optional.of(mockTourSchedule));
//        when(tourDiscountRepository.findFirstByTourSchedule_IdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndDeletedFalse(
//                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)
//        )).thenReturn(Optional.empty());
//
//        // Giả lập hàm save ném ra lỗi
//        when(tourDiscountRepository.save(any(TourDiscount.class))).thenThrow(new RuntimeException("Database connection error"));
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.createDiscount(requestDTO);
//        });
//
//        assertEquals(Constants.Message.TOUR_DISCOUNT_CREATE_FAIL, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
//    }
//
//    @Test
//    @DisplayName("[getDiscounts] Valid Input: Lấy danh sách thành công khi không có keyword")
//    void getDiscounts_whenNoKeyword_shouldSucceed() {
//        System.out.println("Test Case: Valid Input - Lấy danh sách thành công khi không có keyword.");
//        // Arrange
//        String keyword = null;
//        int page = 0;
//        int size = 10;
//
//        TourDiscount discount1 = TourDiscount.builder().id(1L).build();
//        TourDiscount discount2 = TourDiscount.builder().id(2L).build();
//        Page<TourDiscount> mockPage = new PageImpl<>(List.of(discount1, discount2));
//
//        when(tourDiscountRepository.searchActiveDiscounts(eq(keyword), any(LocalDateTime.class), any(Pageable.class)))
//                .thenReturn(mockPage);
//        when(tourDiscountMapper.toSummaryDTO(any(TourDiscount.class))).thenReturn(new TourDiscountSummaryDTO());
//
//        // Act
//        GeneralResponse<PagingDTO<TourDiscountSummaryDTO>> response = tourDiscountService.getDiscounts(keyword, page, size);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK.value(), response.getStatus());
//        assertEquals(Constants.Message.TOUR_DISCOUNT_LIST_SUCCESS, response.getMessage());
//        assertNotNull(response.getData());
//        assertEquals(2, response.getData().getTotal());
//        assertEquals(2, response.getData().getItems().size());
//
//        // Verify
//        verify(tourDiscountRepository, times(1)).searchActiveDiscounts(eq(keyword), any(LocalDateTime.class), any(Pageable.class));
//        System.out.println("Log: " + Constants.Message.SUCCESS);
//    }
//
//    @Test
//    @DisplayName("[getDiscounts] Valid Input: Lấy danh sách thành công với keyword hợp lệ")
//    void getDiscounts_whenWithKeyword_shouldSucceed() {
//        System.out.println("Test Case: Valid Input - Lấy danh sách thành công với keyword hợp lệ.");
//        // Arrange
//        String keyword = "Hanoi";
//        int page = 0;
//        int size = 5;
//
//        Page<TourDiscount> mockPage = new PageImpl<>(List.of(TourDiscount.builder().id(1L).build()));
//        when(tourDiscountRepository.searchActiveDiscounts(eq(keyword), any(LocalDateTime.class), any(Pageable.class)))
//                .thenReturn(mockPage);
//        when(tourDiscountMapper.toSummaryDTO(any(TourDiscount.class))).thenReturn(new TourDiscountSummaryDTO());
//
//        // Act
//        GeneralResponse<PagingDTO<TourDiscountSummaryDTO>> response = tourDiscountService.getDiscounts(keyword, page, size);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK.value(), response.getStatus());
//        assertEquals(1, response.getData().getTotal());
//
//        // Verify
//        verify(tourDiscountRepository, times(1)).searchActiveDiscounts(eq(keyword), any(LocalDateTime.class), any(Pageable.class));
//        System.out.println("Log: " + Constants.Message.SUCCESS);
//    }
//
//    @Test
//    @DisplayName("[getDiscounts] Valid Input: Trả về trang rỗng khi không có dữ liệu")
//    void getDiscounts_whenNoData_shouldReturnEmptyPage() {
//        System.out.println("Test Case: Valid Input - Trả về trang rỗng khi không có dữ liệu.");
//        // Arrange
//        when(tourDiscountRepository.searchActiveDiscounts(any(), any(LocalDateTime.class), any(Pageable.class)))
//                .thenReturn(Page.empty());
//
//        // Act
//        GeneralResponse<PagingDTO<TourDiscountSummaryDTO>> response = tourDiscountService.getDiscounts(null, 0, 10);
//
//        // Assert
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK.value(), response.getStatus());
//        assertNotNull(response.getData());
//        assertEquals(0, response.getData().getTotal());
//        assertTrue(response.getData().getItems().isEmpty());
//
//        // Verify
//        verify(tourDiscountMapper, never()).toSummaryDTO(any());
//        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + Constants.Message.NO_SERVICES_AVAILABLE);
//    }
//
//    @Test
//    @DisplayName("[getDiscounts] Invalid Input: Thất bại khi page là số âm")
//    void getDiscounts_whenPageIsNegative_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi page là số âm.");
//        // Arrange
//        int invalidPage = -1;
//
//        // Act & Assert
//        // SỬA LỖI: Kỳ vọng BusinessException vì service có khối try-catch(Exception)
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.getDiscounts(null, invalidPage, 10);
//        });
//
//        // Kiểm tra thông báo lỗi của BusinessException
//        assertEquals(Constants.Message.TOUR_DISCOUNT_LIST_FAIL, exception.getResponseMessage());
//        // Kiểm tra dữ liệu lỗi gốc (optional but good practice)
//        assertTrue(exception.getResponseData().toString().contains("Page index must not be less than zero"));
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Page không được là số âm.");
//    }
//
//    @Test
//    @DisplayName("[getDiscounts] Invalid Input: Thất bại khi size nhỏ hơn 1")
//    void getDiscounts_whenSizeIsLessThanOne_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi size nhỏ hơn 1.");
//        // Arrange
//        int invalidSize = 0;
//
//        // Act & Assert
//        // SỬA LỖI: Kỳ vọng BusinessException vì service có khối try-catch(Exception)
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.getDiscounts(null, 0, invalidSize);
//        });
//
//        // Kiểm tra thông báo lỗi của BusinessException
//        assertEquals(Constants.Message.TOUR_DISCOUNT_LIST_FAIL, exception.getResponseMessage());
//        // Kiểm tra dữ liệu lỗi gốc
//        assertTrue(exception.getResponseData().toString().contains("Page size must not be less than one"));
//        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Size phải lớn hơn 0.");
//    }
//
//    @Test
//    @DisplayName("[getDiscounts] Invalid Input: Thất bại khi repository ném ra lỗi")
//    void getDiscounts_whenRepositoryFails_shouldThrowBusinessException() {
//        System.out.println("Test Case: Invalid Input - Thất bại khi repository ném ra lỗi.");
//        // Arrange
//        when(tourDiscountRepository.searchActiveDiscounts(any(), any(LocalDateTime.class), any(Pageable.class)))
//                .thenThrow(new RuntimeException("Database connection error"));
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourDiscountService.getDiscounts(null, 0, 10);
//        });
//
//        assertEquals(Constants.Message.TOUR_DISCOUNT_LIST_FAIL, exception.getResponseMessage());
//        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
//    }
//}