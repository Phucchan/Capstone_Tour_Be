//package com.fpt.capstone.tourism.service.impl;
//
//import com.fpt.capstone.tourism.dto.general.GeneralResponse;
//import com.fpt.capstone.tourism.dto.request.tourManager.TourCreateManagerRequestDTO;
//import com.fpt.capstone.tourism.dto.response.tourManager.TourDetailManagerDTO;
//import com.fpt.capstone.tourism.exception.common.BusinessException;
//import com.fpt.capstone.tourism.helper.IHelper.TourHelper;
//import com.fpt.capstone.tourism.mapper.booking.RequestBookingMapper;
//import com.fpt.capstone.tourism.model.Location;
//import com.fpt.capstone.tourism.model.RequestBooking;
//import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
//import com.fpt.capstone.tourism.model.enums.TourStatus;
//import com.fpt.capstone.tourism.model.enums.TourType;
//import com.fpt.capstone.tourism.model.tour.Tour;
//import com.fpt.capstone.tourism.model.tour.TourDay;
//import com.fpt.capstone.tourism.model.tour.TourTheme;
//import com.fpt.capstone.tourism.repository.LocationRepository;
//import com.fpt.capstone.tourism.repository.RequestBookingRepository;
//import com.fpt.capstone.tourism.repository.TourManagementRepository;
//import com.fpt.capstone.tourism.repository.tour.TourDayRepository;
//import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
//import com.fpt.capstone.tourism.service.S3Service;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TourManagementServiceImplTest {
//
//    @InjectMocks
//    private TourManagementServiceImpl tourManagementService;
//
//    // --- Mocks for all dependencies ---
//    @Mock private TourManagementRepository tourRepository;
//    @Mock private LocationRepository locationRepository;
//    @Mock private TourThemeRepository tourThemeRepository;
//    @Mock private TourDayRepository tourDayRepository;
//    @Mock private RequestBookingRepository requestBookingRepository;
//    @Mock private S3Service s3Service;
//    @Mock private TourHelper tourHelper;
//    @Mock private RequestBookingMapper requestBookingMapper; // Used in buildDetailDTO
//
//    @BeforeEach
//    void setUp() {
//        // Set the value for @Value("${aws.s3.bucket-url}")
//        ReflectionTestUtils.setField(tourManagementService, "bucketUrl", "https://my-test-bucket.s3.amazonaws.com");
//    }
//
//    @Test
//    @DisplayName("[createTour] Normal Case: Tạo tour FIXED thành công với đầy đủ thông tin và file ảnh")
//    void createTour_FixedTour_WithAllDetails_ShouldSucceed() {
//        // Arrange
//        // 1. Tham số đầu vào: DTO và file ảnh
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder()
//                .name("  Tour Khám Phá Vịnh Hạ Long  ")
//                .description("Hành trình 2 ngày 1 đêm trên du thuyền 5 sao.")
//                .tourType(TourType.FIXED)
//                .departLocationId(1L)
//                .destinationLocationIds(List.of(2L, 3L))
//                .tourThemeIds(List.of(101L))
//                .build();
//        MultipartFile file = new MockMultipartFile("image", "halong.jpg", "image/jpeg", "some-image-bytes".getBytes());
//
//        // 2. Giả lập (mock) các dependency
//        when(tourHelper.generateTourCode()).thenReturn("T-HL2N1D");
//        when(s3Service.uploadFile(any(MultipartFile.class), eq("tours"))).thenReturn("tours/random-key.jpg");
//
//        Location departLocation = Location.builder().id(1L).name("Hà Nội").build();
//        Location destLocation1 = Location.builder().id(2L).name("Vịnh Hạ Long").build();
//        Location destLocation2 = Location.builder().id(3L).name("Đảo Tuần Châu").build();
//        when(locationRepository.findById(1L)).thenReturn(Optional.of(departLocation));
//        when(locationRepository.findById(2L)).thenReturn(Optional.of(destLocation1));
//        when(locationRepository.findById(3L)).thenReturn(Optional.of(destLocation2));
//
//        TourTheme theme = TourTheme.builder().id(101L).name("Biển Đảo").build();
//        when(tourThemeRepository.findAllById(List.of(101L))).thenReturn(List.of(theme));
//
//        // Bắt (capture) đối tượng Tour được lưu để kiểm tra
//        ArgumentCaptor<Tour> tourCaptor = ArgumentCaptor.forClass(Tour.class);
//        when(tourRepository.save(tourCaptor.capture())).thenAnswer(invocation -> {
//            Tour savedTour = invocation.getArgument(0);
//            savedTour.setId(99L); // Giả lập việc lưu và có ID trả về
//            return savedTour;
//        });
//
//        // Giả lập cho hàm buildDetailDTO được gọi ở cuối
//        when(tourRepository.findById(99L)).thenAnswer(invocation -> Optional.of(tourCaptor.getValue()));
//        when(tourDayRepository.findByTourIdOrderByDayNumberAsc(99L)).thenReturn(Collections.emptyList());
//
//        // Act
//        GeneralResponse<TourDetailManagerDTO> response = tourManagementService.createTour(requestDTO, file);
//
//        // Assert
//        // 3. Kiểm tra kết quả trả về
//        assertNotNull(response);
//        assertNotNull(response.getData());
//        assertEquals("Tour Khám Phá Vịnh Hạ Long", response.getData().getName()); // Tên đã được trim
//        assertEquals("T-HL2N1D", response.getData().getCode());
//        assertEquals(TourStatus.DRAFT.name(), response.getData().getTourStatus());
//        assertEquals(TourType.FIXED.name(), response.getData().getTourType());
//        assertEquals("https://my-test-bucket.s3.amazonaws.com/tours/random-key.jpg", response.getData().getThumbnailUrl());
//        assertEquals(2, response.getData().getDurationDays()); // 2 điểm đến
//
//        // 4. Xác minh (verify) các tương tác
//        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), eq("tours"));
//        verify(tourRepository, times(2)).save(any(Tour.class)); // 1 lần cho tour, 1 lần cập nhật duration
//        verify(tourDayRepository, times(2)).save(any(TourDay.class)); // 1 lần cho mỗi điểm đến
//        verify(requestBookingRepository, never()).findById(anyLong()); // Không được gọi với tour FIXED
//    }
//
//    @Test
//    @DisplayName("[createTour] Normal Case: Tạo tour CUSTOM thành công từ một request booking")
//    void createTour_CustomTour_FromRequest_ShouldSucceed() {
//        // Arrange
//        // 1. Tham số đầu vào: DTO cho tour CUSTOM
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder()
//                .name("Tour Riêng Theo Yêu Cầu")
//                .tourType(TourType.CUSTOM)
//                .requestBookingId(55L)
//                .build();
//
//        // 2. Giả lập dependency
//        RequestBooking mockRequest = RequestBooking.builder().id(55L).status(RequestBookingStatus.PENDING).build();
//        when(requestBookingRepository.findById(55L)).thenReturn(Optional.of(mockRequest));
//
//        ArgumentCaptor<Tour> tourCaptor = ArgumentCaptor.forClass(Tour.class);
//        when(tourRepository.save(tourCaptor.capture())).thenAnswer(invocation -> {
//            Tour savedTour = invocation.getArgument(0);
//            savedTour.setId(100L);
//            savedTour.setRequestBooking(mockRequest); // Liên kết với request
//            return savedTour;
//        });
//
//        when(tourRepository.findById(100L)).thenAnswer(invocation -> Optional.of(tourCaptor.getValue()));
//        when(requestBookingMapper.toDTO(any(RequestBooking.class))).thenReturn(null); // Đơn giản hóa test
//
//        // Act
//        GeneralResponse<TourDetailManagerDTO> response = tourManagementService.createTour(requestDTO, null);
//
//        // Assert
//        // 3. Kiểm tra kết quả
//        assertNotNull(response);
//        assertNotNull(response.getData());
//        assertEquals(100L, response.getData().getId());
//        assertEquals(55L, response.getData().getRequestBookingId());
//        assertEquals(TourType.CUSTOM.name(), response.getData().getTourType());
//
//        // 4. Xác minh trạng thái của request booking đã được cập nhật
//        verify(requestBookingRepository, times(1)).findById(55L);
//        ArgumentCaptor<RequestBooking> requestCaptor = ArgumentCaptor.forClass(RequestBooking.class);
//        verify(requestBookingRepository, times(1)).save(requestCaptor.capture());
//        assertEquals(RequestBookingStatus.COMPLETED, requestCaptor.getValue().getStatus());
//    }
//
//    @Test
//    @DisplayName("[createTour] Abnormal Case: Tham số name rỗng")
//    void createTour_WhenNameIsEmpty_ShouldThrowException() {
//        // Arrange
//        // 1. Tham số đầu vào: name là chuỗi rỗng
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder().name("  ").build();
//
//        // Act & Assert
//        // 2. Thực thi và kiểm tra exception
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourManagementService.createTour(requestDTO, null);
//        });
//
//        // 3. Kiểm tra nội dung exception
//        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getHttpCode());
//        assertEquals("Tour name is required", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("[createTour] Abnormal Case: Tour CUSTOM nhưng tham số requestBookingId là null")
//    void createTour_CustomTour_WithoutRequestBookingId_ShouldThrowException() {
//        // Arrange
//        // 1. Tham số đầu vào: tourType=CUSTOM, requestBookingId=null
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder()
//                .name("Invalid Custom Tour")
//                .tourType(TourType.CUSTOM)
//                .requestBookingId(null) // Thiếu ID
//                .build();
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourManagementService.createTour(requestDTO, null);
//        });
//
//        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getHttpCode());
//        assertEquals("Custom tour requires requestBookingId", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("[createTour] Abnormal Case: Tour FIXED nhưng lại có tham số requestBookingId")
//    void createTour_FixedTour_WithRequestBookingId_ShouldThrowException() {
//        // Arrange
//        // 1. Tham số đầu vào: tourType=FIXED, requestBookingId có giá trị
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder()
//                .name("Invalid Fixed Tour")
//                .tourType(TourType.FIXED)
//                .requestBookingId(123L) // Không được phép có
//                .build();
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourManagementService.createTour(requestDTO, null);
//        });
//
//        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getHttpCode());
//        assertEquals("Fixed tour cannot have requestBookingId", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("[createTour] Abnormal Case: Tham số departLocationId không tồn tại")
//    void createTour_WhenDepartLocationNotFound_ShouldThrowException() {
//        // Arrange
//        // 1. Tham số đầu vào: departLocationId không tồn tại
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder()
//                .name("Tour Lỗi")
//                .departLocationId(999L) // ID không tồn tại
//                .build();
//
//        // 2. Giả lập repository trả về rỗng
//        when(locationRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourManagementService.createTour(requestDTO, null);
//        });
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
//        assertEquals("Depart location not found", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("[createTour] Abnormal Case: Một trong các tham số destinationLocationIds không tồn tại")
//    void createTour_WhenDestinationLocationNotFound_ShouldThrowException() {
//        // Arrange
//        // 1. Tham số đầu vào: destinationLocationIds chứa ID không tồn tại
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder()
//                .name("Tour Lỗi Điểm Đến")
//                .destinationLocationIds(List.of(1L, 999L)) // 999 là ID không tồn tại
//                .build();
//
//        // 2. Giả lập các dependency cần thiết trước khi xảy ra lỗi
//        when(tourHelper.generateTourCode()).thenReturn("T-ERROR");
//        // Lệnh save đầu tiên sẽ được gọi trước khi duyệt qua các điểm đến
//        when(tourRepository.save(any(Tour.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        when(locationRepository.findById(1L)).thenReturn(Optional.of(Location.builder().id(1L).build()));
//        when(locationRepository.findById(999L)).thenReturn(Optional.empty()); // Giả lập không tìm thấy
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourManagementService.createTour(requestDTO, null);
//        });
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
//        assertEquals("Location not found", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("[createTour] Abnormal Case: Tour CUSTOM với requestBookingId không tồn tại")
//    void createTour_CustomTour_WithNonExistentRequestBookingId_ShouldThrowException() {
//        // Arrange
//        // 1. Tham số đầu vào: tourType=CUSTOM, requestBookingId không tồn tại
//        TourCreateManagerRequestDTO requestDTO = TourCreateManagerRequestDTO.builder()
//                .name("Tour với Request không tồn tại")
//                .tourType(TourType.CUSTOM)
//                .requestBookingId(999L) // ID không tồn tại
//                .build();
//
//        // 2. Giả lập repository trả về rỗng
//        when(requestBookingRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        BusinessException exception = assertThrows(BusinessException.class, () -> {
//            tourManagementService.createTour(requestDTO, null);
//        });
//
//        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
//        assertEquals("Request booking not found", exception.getMessage());
//    }
//}