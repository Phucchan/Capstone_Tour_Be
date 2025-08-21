package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingDTO;
import com.fpt.capstone.tourism.dto.request.RequestBookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.RequestBookingNotificationDTO;
import com.fpt.capstone.tourism.mapper.booking.RequestBookingMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.RequestBooking;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.enums.RequestBookingStatus;
import com.fpt.capstone.tourism.model.enums.RoomCategory;
import com.fpt.capstone.tourism.model.enums.TourTransport;
import com.fpt.capstone.tourism.model.tour.TourTheme;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.RequestBookingRepository;
import com.fpt.capstone.tourism.repository.tour.TourThemeRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestBookingServiceImplTest {

    @InjectMocks
    private RequestBookingServiceImpl requestBookingService;

    // Mock các dependency cần thiết
    @Mock private RequestBookingRepository requestBookingRepository;
    @Mock private RequestBookingMapper requestBookingMapper;
    @Mock private UserRepository userRepository;
    @Mock private TourThemeRepository tourThemeRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;
    @Mock private RequestBookingVerificationService verificationService;

    private RequestBookingDTO validRequestDTO;

    @BeforeEach
    void setUp() {
        // Khởi tạo một DTO hợp lệ để làm mẫu
        validRequestDTO = RequestBookingDTO.builder()
                .userId(1L)
                .departureLocationId(10L)
                .destinationLocationIds(List.of(20L))
                .tourThemeIds(List.of(30L))
                .desiredServices("Hotel, Restaurant")
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(15))
                .transport(TourTransport.CAR)
                .adults(2)
                .children(1)
                .infants(0)
                .toddlers(0)
                .hotelRooms(1)
                .roomCategory(RoomCategory.Standard)
                .customerName("Nguyễn Văn A")
                .customerEmail("test@example.com")
                .customerPhone("0987654321")
                .verificationCode("123456")
                .build();
    }

    // =================================================================
    // 1. Normal Case - Trường hợp hợp lệ
    // =================================================================

    @Test
    @DisplayName("[createRequest] Normal Case: Tạo yêu cầu thành công với dữ liệu hợp lệ")
    void createRequest_whenRequestIsValid_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Tạo yêu cầu thành công với dữ liệu hợp lệ.");
        // Arrange
        // Giả lập mã xác thực là đúng
        when(verificationService.verifyCode(validRequestDTO.getCustomerEmail(), validRequestDTO.getVerificationCode())).thenReturn(true);

        // Giả lập các hàm find của repository trả về dữ liệu (để hàm không bị lỗi)
        when(userRepository.findUserById(any())).thenReturn(Optional.of(new User()));
        when(locationRepository.findById(any())).thenReturn(Optional.of(new Location()));
        when(locationRepository.findAllById(any())).thenReturn(Collections.singletonList(new Location()));
        when(tourThemeRepository.findAllById(any())).thenReturn(Collections.singletonList(new TourTheme()));

        // Giả lập mapper và hàm save
        when(requestBookingMapper.toEntity(any())).thenReturn(new RequestBooking());
        when(requestBookingRepository.save(any(RequestBooking.class))).thenReturn(new RequestBooking());
        when(requestBookingMapper.toDTO(any())).thenReturn(new RequestBookingDTO());

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.createRequest(validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals("Request saved", response.getMessage());
        assertNotNull(response.getData());

        // Verify
        verify(requestBookingRepository, times(1)).save(any(RequestBooking.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/request-bookings"), any(RequestBookingNotificationDTO.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    // =================================================================
    // 2. Abnormal Cases - Các trường hợp không hợp lệ
    // =================================================================

    // Phương thức helper để tạo ra các DTO không hợp lệ cho parameterized test
    private static RequestBookingDTO cloneAndModify(RequestBookingDTO original, Consumer<RequestBookingDTO> modifier) {
        RequestBookingDTO clone = original.toBuilder().build(); // Sử dụng toBuilder để tạo bản sao
        modifier.accept(clone);
        return clone;
    }

    // Nguồn dữ liệu cho parameterized test
    private static Stream<Arguments> provideInvalidRequestDTOs() {
        // Tạo một DTO hợp lệ làm cơ sở
        RequestBookingDTO baseDto = RequestBookingDTO.builder()
                .userId(1L).departureLocationId(10L).destinationLocationIds(List.of(20L))
                .tourThemeIds(List.of(30L)).desiredServices("Services").startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1)).transport(TourTransport.CAR).adults(1).children(0)
                .infants(0).toddlers(0).hotelRooms(1).roomCategory(RoomCategory.Standard).customerName("Name")
                .customerEmail("email@test.com").customerPhone("1234567890").verificationCode("123")
                .build();

        return Stream.of(
                Arguments.of(cloneAndModify(baseDto, dto -> dto.setDestinationLocationIds(null)), "destinationLocationIds is null"),
                Arguments.of(cloneAndModify(baseDto, dto -> dto.setDestinationLocationIds(Collections.emptyList())), "destinationLocationIds is empty"),
                Arguments.of(cloneAndModify(baseDto, dto -> dto.setCustomerName(null)), "customerName is null"),
                Arguments.of(cloneAndModify(baseDto, dto -> dto.setCustomerName("  ")), "customerName is blank"),
                Arguments.of(cloneAndModify(baseDto, dto -> dto.setVerificationCode(null)), "verificationCode is null")
        );
    }

    @ParameterizedTest(name = "Thất bại khi {1}")
    @MethodSource("provideInvalidRequestDTOs")
    @DisplayName("[createRequest] Abnormal Case: Thất bại khi thiếu các trường bắt buộc")
    void createRequest_whenRequiredFieldIsMissing_shouldReturnBadRequest(RequestBookingDTO invalidDto, String testCaseName) {
        System.out.println("Test Case: Abnormal Input - " + testCaseName);
        // Arrange (Không cần arrange gì thêm)

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.createRequest(invalidDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertEquals("Missing required fields", response.getMessage()); // Kỳ vọng thông báo lỗi chung
        assertNull(response.getData());

        // Verify
        verify(requestBookingRepository, never()).save(any());
        verify(verificationService, never()).verifyCode(anyString(), anyString());
        // SỬA LỖI: Sử dụng hằng số cho thông báo log
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.UserExceptionInformation.USER_INFORMATION_NULL_OR_EMPTY);
    }

    @Test
    @DisplayName("[createRequest] Abnormal Case: Thất bại khi userId là null")
    void createRequest_whenUserIdIsNull_shouldReturnSpecificError() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi userId là null.");
        // Arrange
        validRequestDTO.setUserId(null);

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.createRequest(validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertEquals("User account is required", response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(requestBookingRepository, never()).save(any());
        // SỬA LỖI: Sử dụng hằng số cho thông báo log
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.USER_INFO_NOT_FOUND);
    }


    @Test
    @DisplayName("[createRequest] Abnormal Case: Thất bại khi mã xác thực không hợp lệ")
    void createRequest_whenVerificationCodeIsInvalid_shouldReturnBadRequest() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi mã xác thực không hợp lệ.");
        // Arrange
        // Giả lập mã xác thực là sai
        when(verificationService.verifyCode(validRequestDTO.getCustomerEmail(), validRequestDTO.getVerificationCode())).thenReturn(false);

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.createRequest(validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertEquals("Invalid verification code", response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(requestBookingRepository, never()).save(any());
        // SỬA LỖI: Sử dụng hằng số cho thông báo log
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.INVALID_CONFIRMATION_TOKEN_MESSAGE);
    }
    // =================================================================
    // 3. Test Cases for sendVerificationCode
    // =================================================================

    @Test
    @DisplayName("[sendVerificationCode] Normal Case: Gửi mã thành công với email hợp lệ")
    void sendVerificationCode_whenEmailIsValid_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Gửi mã thành công với email hợp lệ.");
        // Arrange
        String validEmail = "test@example.com";
        // Giả lập hàm sendCode không làm gì cả khi được gọi
        doNothing().when(verificationService).sendCode(validEmail);

        // Act
        GeneralResponse<String> response = requestBookingService.sendVerificationCode(validEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals("Verification code sent", response.getMessage());
        assertNull(response.getData());

        // Verify
        // Đảm bảo hàm sendCode đã được gọi đúng 1 lần với email chính xác
        verify(verificationService, times(1)).sendCode(validEmail);
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    // THÊM MỚI: Test case cho email sai định dạng
    @Test
    @DisplayName("[sendVerificationCode] Normal Case: Vẫn thành công khi email sai định dạng (service không validate)")
    void sendVerificationCode_whenEmailIsInvalidFormat_shouldStillSucceed() {
        System.out.println("Test Case: Normal Input - Vẫn thành công khi email sai định dạng.");
        // Arrange
        String invalidFormatEmail = "this-is-not-an-email";
        doNothing().when(verificationService).sendCode(invalidFormatEmail);

        // Act
        GeneralResponse<String> response = requestBookingService.sendVerificationCode(invalidFormatEmail);

        // Assert
        // Hàm vẫn trả về thành công vì nó không kiểm tra định dạng
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals("Verification code sent", response.getMessage());

        // Verify
        // Quan trọng là phải xác minh rằng hàm sendCode vẫn được gọi với email sai định dạng
        verify(verificationService, times(1)).sendCode(invalidFormatEmail);
        System.out.println("Log: " + Constants.Message.SUCCESS + ". (Lưu ý: Service không kiểm tra định dạng email, chỉ chuyển tiếp cho dependency).");
    }

    @Test
    @DisplayName("[sendVerificationCode] Abnormal Case: Thất bại khi email là null")
    void sendVerificationCode_whenEmailIsNull_shouldReturnBadRequest() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi email là null.");
        // Arrange
        String nullEmail = null;

        // Act
        GeneralResponse<String> response = requestBookingService.sendVerificationCode(nullEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertEquals("Email is required", response.getMessage());

        // Verify
        // Đảm bảo hàm sendCode không bao giờ được gọi
        verify(verificationService, never()).sendCode(anyString());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.EMPTY_EMAIL);
    }

    @ParameterizedTest(name = "Thất bại khi email là ''{0}''")
    @ValueSource(strings = {"", "   "})
    @DisplayName("[sendVerificationCode] Abnormal Case: Thất bại khi email là chuỗi rỗng hoặc khoảng trắng")
    void sendVerificationCode_whenEmailIsBlank_shouldReturnBadRequest(String blankEmail) {
        System.out.println("Test Case: Abnormal Input - Thất bại khi email là chuỗi rỗng hoặc khoảng trắng.");
        // Arrange (Không cần arrange gì thêm)

        // Act
        GeneralResponse<String> response = requestBookingService.sendVerificationCode(blankEmail);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertEquals("Email is required", response.getMessage());

        // Verify
        verify(verificationService, never()).sendCode(anyString());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.EMPTY_EMAIL);
    }
    // =================================================================
    // 4. Test Cases for rejectRequest
    // =================================================================

    @Test
    @DisplayName("[rejectRequest] Normal Case: Từ chối yêu cầu thành công với lý do hợp lệ")
    void rejectRequest_whenRequestIsValid_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Từ chối yêu cầu thành công với lý do hợp lệ.");
        // Arrange
        Long bookingId = 1L;
        String validReason = "Không đủ nguồn lực để đáp ứng.";
        RequestBooking existingBooking = RequestBooking.builder()
                .id(bookingId)
                .status(RequestBookingStatus.PENDING) // Trạng thái ban đầu
                .build();

        // Giả lập repository tìm thấy booking
        when(requestBookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));

        // Sử dụng ArgumentCaptor để "bắt" lại đối tượng được lưu
        ArgumentCaptor<RequestBooking> bookingCaptor = ArgumentCaptor.forClass(RequestBooking.class);
        when(requestBookingRepository.save(bookingCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(requestBookingMapper.toDTO(any(RequestBooking.class))).thenReturn(new RequestBookingDTO());

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.rejectRequest(bookingId, validReason);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals("Request rejected", response.getMessage());
        assertNotNull(response.getData());

        // Kiểm tra đối tượng đã được "bắt"
        RequestBooking capturedBooking = bookingCaptor.getValue();
        assertEquals(RequestBookingStatus.REJECTED, capturedBooking.getStatus(), "Trạng thái phải được cập nhật thành REJECTED.");
        assertEquals(validReason, capturedBooking.getReason(), "Lý do từ chối phải được gán chính xác.");

        // Verify
        verify(requestBookingRepository, times(1)).findById(bookingId);
        verify(requestBookingRepository, times(1)).save(any(RequestBooking.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[rejectRequest] Abnormal Case: Thất bại khi không tìm thấy yêu cầu (ID không tồn tại)")
    void rejectRequest_whenIdNotFound_shouldReturnNotFound() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi không tìm thấy yêu cầu.");
        // Arrange
        Long nonExistentId = 99L;
        String reason = "Some reason";

        // Giả lập repository không tìm thấy booking
        when(requestBookingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.rejectRequest(nonExistentId, reason);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
        assertEquals("Request not found", response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(requestBookingRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.BOOKING_NOT_FOUND);
    }

    @ParameterizedTest(name = "Thất bại khi lý do là ''{0}''")
    @ValueSource(strings = {"", "   "})
    @DisplayName("[rejectRequest] Abnormal Case: Thất bại khi lý do là chuỗi rỗng hoặc khoảng trắng")
    void rejectRequest_whenReasonIsBlank_shouldReturnBadRequest(String blankReason) {
        System.out.println("Test Case: Abnormal Input - Thất bại khi lý do là chuỗi rỗng hoặc khoảng trắng.");
        // Arrange
        Long bookingId = 1L;
        RequestBooking existingBooking = RequestBooking.builder().id(bookingId).build();
        when(requestBookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.rejectRequest(bookingId, blankReason);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertEquals("Reason is required", response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(requestBookingRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.UserExceptionInformation.USER_INFORMATION_NULL_OR_EMPTY);
    }

    @Test
    @DisplayName("[rejectRequest] Abnormal Case: Thất bại khi lý do là null")
    void rejectRequest_whenReasonIsNull_shouldReturnBadRequest() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi lý do là null.");
        // Arrange
        Long bookingId = 1L;
        RequestBooking existingBooking = RequestBooking.builder().id(bookingId).build();
        when(requestBookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.rejectRequest(bookingId, null);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getCode());
        assertEquals("Reason is required", response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(requestBookingRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.UserExceptionInformation.USER_INFORMATION_NULL_OR_EMPTY);
    }
    // =================================================================
    // 5. Test Cases for getRequest
    // =================================================================

    @Test
    @DisplayName("[getRequest] Normal Case: Lấy yêu cầu thành công với ID hợp lệ")
    void getRequest_whenIdIsValid_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy yêu cầu thành công với ID hợp lệ.");
        // Arrange
        Long bookingId = 1L;
        RequestBooking existingBooking = RequestBooking.builder()
                .id(bookingId)
                .customerName("Test Customer")
                .build();
        RequestBookingDTO expectedDto = RequestBookingDTO.builder()
                .id(bookingId)
                .customerName("Test Customer")
                .build();

        // Giả lập repository tìm thấy booking
        when(requestBookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
        // Giả lập mapper chuyển đổi entity sang DTO
        when(requestBookingMapper.toDTO(existingBooking)).thenReturn(expectedDto);

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.getRequest(bookingId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(HttpStatus.OK.getReasonPhrase(), response.getMessage());
        assertNotNull(response.getData());
        assertEquals(expectedDto.getId(), response.getData().getId());
        assertEquals(expectedDto.getCustomerName(), response.getData().getCustomerName());

        // Verify
        verify(requestBookingRepository, times(1)).findById(bookingId);
        verify(requestBookingMapper, times(1)).toDTO(existingBooking);
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getRequest] Abnormal Case: Thất bại khi không tìm thấy yêu cầu (ID không tồn tại)")
    void getRequest_whenIdIsNotFound_shouldReturnNotFound() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi không tìm thấy yêu cầu.");
        // Arrange
        Long nonExistentId = 99L;

        // Giả lập repository không tìm thấy booking
        when(requestBookingRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        GeneralResponse<RequestBookingDTO> response = requestBookingService.getRequest(nonExistentId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getCode());
        assertEquals("Request not found", response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(requestBookingRepository, times(1)).findById(nonExistentId);
        // Mapper không bao giờ được gọi
        verify(requestBookingMapper, never()).toDTO(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.BOOKING_NOT_FOUND);
    }

    @Test
    @DisplayName("[getRequest] Abnormal Case: Thất bại khi ID truyền vào là null")
    void getRequest_whenIdIsNull_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi ID truyền vào là null.");
        // Arrange
        Long nullId = null;
        // Giả lập rằng khi truyền ID null vào repository, nó sẽ ném ra một lỗi.
        // Đây là hành vi mặc định của Spring Data JPA.
        when(requestBookingRepository.findById(nullId)).thenThrow(new IllegalArgumentException("ID must not be null!"));

        // Act & Assert
        // Kỳ vọng một IllegalArgumentException sẽ được ném ra từ service
        // vì không có khối try-catch trong hàm getRequest
        assertThrows(IllegalArgumentException.class, () -> {
            requestBookingService.getRequest(nullId);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: ID không được là null.");
    }
    // =================================================================
    // 6. Test Cases for getRequestsByStatus
    // =================================================================

    @Test
    @DisplayName("[getRequestsByStatus] Normal Case: Lấy danh sách thành công với status và không có keyword")
    void getRequestsByStatus_withStatusAndWithoutSearch_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy danh sách thành công với status và không có keyword.");
        // Arrange
        RequestBookingStatus status = RequestBookingStatus.PENDING;
        int page = 0;
        int size = 10;
        String search = null;

        RequestBooking booking1 = RequestBooking.builder().id(1L).tourTheme("Theme A").status(status).build();
        RequestBooking booking2 = RequestBooking.builder().id(2L).tourTheme("Theme B").status(status).build();
        Page<RequestBooking> mockPage = new PageImpl<>(List.of(booking1, booking2), PageRequest.of(page, size), 2);

        // Giả lập repository được gọi không có keyword
        when(requestBookingRepository.findByStatus(eq(status), any(Pageable.class))).thenReturn(mockPage);

        // Act
        GeneralResponse<PagingDTO<RequestBookingSummaryDTO>> response = requestBookingService.getRequestsByStatus(status, page, size, search);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());

        PagingDTO<RequestBookingSummaryDTO> pagingDTO = response.getData();
        assertNotNull(pagingDTO);
        assertEquals(2, pagingDTO.getTotal());
        assertEquals(2, pagingDTO.getItems().size());

        // Verify
        verify(requestBookingRepository, times(1)).findByStatus(eq(status), any(Pageable.class));
        verify(requestBookingRepository, never()).searchByStatusAndKeyword(any(), anyString(), any());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getRequestsByStatus] Normal Case: Lấy danh sách thành công với status và có keyword")
    void getRequestsByStatus_withStatusAndWithSearch_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy danh sách thành công với status và có keyword.");
        // Arrange
        RequestBookingStatus status = RequestBookingStatus.ACCEPTED;
        int page = 0;
        int size = 5;
        String search = "hanoi";

        RequestBooking booking = RequestBooking.builder().id(1L).tourTheme("Tour Hanoi").status(status).build();
        Page<RequestBooking> mockPage = new PageImpl<>(List.of(booking), PageRequest.of(page, size), 1);

        // Giả lập repository được gọi có keyword
        when(requestBookingRepository.searchByStatusAndKeyword(eq(status), eq(search), any(Pageable.class))).thenReturn(mockPage);

        // Act
        GeneralResponse<PagingDTO<RequestBookingSummaryDTO>> response = requestBookingService.getRequestsByStatus(status, page, size, search);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(1, response.getData().getTotal());
        assertEquals("Tour Hanoi", response.getData().getItems().get(0));

        // Verify
        verify(requestBookingRepository, never()).findByStatus(any(), any(Pageable.class));
        verify(requestBookingRepository, times(1)).searchByStatusAndKeyword(eq(status), eq(search), any());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getRequestsByStatus] Normal Case: Trả về danh sách rỗng khi không có yêu cầu nào")
    void getRequestsByStatus_whenNoRequestsFound_shouldReturnEmptyPage() {
        System.out.println("Test Case: Normal Input - Trả về danh sách rỗng khi không có yêu cầu nào.");
        // Arrange
        RequestBookingStatus status = RequestBookingStatus.REJECTED;
        // Giả lập repository trả về một trang rỗng
        when(requestBookingRepository.findByStatus(eq(status), any(Pageable.class))).thenReturn(Page.empty());

        // Act
        GeneralResponse<PagingDTO<RequestBookingSummaryDTO>> response = requestBookingService.getRequestsByStatus(status, 0, 10, null);

        // Assert
        assertNotNull(response);
        PagingDTO<RequestBookingSummaryDTO> pagingDTO = response.getData();
        assertNotNull(pagingDTO);
        assertEquals(0, pagingDTO.getTotal());
        assertTrue(pagingDTO.getItems().isEmpty());
        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + Constants.Message.NO_SERVICES_AVAILABLE);
    }

    @Test
    @DisplayName("[getRequestsByStatus] Abnormal Case: Thất bại khi page là số âm")
    void getRequestsByStatus_whenPageIsNegative_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi page là số âm.");
        // Arrange
        int invalidPage = -1;

        // Act & Assert
        // PageRequest.of() sẽ ném ra lỗi này
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            requestBookingService.getRequestsByStatus(RequestBookingStatus.PENDING, invalidPage, 10, null);
        });

        assertTrue(exception.getMessage().contains("Page index must not be less than zero"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Page không được là số âm.");
    }

    @Test
    @DisplayName("[getRequestsByStatus] Abnormal Case: Thất bại khi size nhỏ hơn 1")
    void getRequestsByStatus_whenSizeIsLessThanOne_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi size nhỏ hơn 1.");
        // Arrange
        int invalidSize = 0;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            requestBookingService.getRequestsByStatus(RequestBookingStatus.PENDING, 0, invalidSize, null);
        });

        assertTrue(exception.getMessage().contains("Page size must not be less than one"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Size phải lớn hơn 0.");
    }

    @Test
    @DisplayName("[getRequestsByStatus] Abnormal Case: Thất bại khi status là null")
    void getRequestsByStatus_whenStatusIsNull_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi status là null.");
        // Arrange
        RequestBookingStatus nullStatus = null;
        // Giả lập repository sẽ ném lỗi khi nhận status là null
        when(requestBookingRepository.findByStatus(isNull(), any(Pageable.class)))
                .thenThrow(new IllegalArgumentException("Status must not be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            requestBookingService.getRequestsByStatus(nullStatus, 0, 10, null);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.TOUR_STATUS_INVALID);
    }

    @Test
    @DisplayName("[getRequestsByStatus] Abnormal Case: Thất bại khi repository ném ra lỗi")
    void getRequestsByStatus_whenRepositoryThrowsException_shouldPropagateException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        // Giả lập repository ném ra một lỗi runtime khi được gọi
        when(requestBookingRepository.findByStatus(any(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        // Kỳ vọng một RuntimeException sẽ được ném ra từ service vì không có khối try-catch
        assertThrows(RuntimeException.class, () -> {
            requestBookingService.getRequestsByStatus(RequestBookingStatus.PENDING, 0, 10, null);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }
}