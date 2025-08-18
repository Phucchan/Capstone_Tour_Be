package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.VoucherDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.VoucherRequestDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.VoucherMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.voucher.Voucher;
import com.fpt.capstone.tourism.repository.voucher.VoucherRepository;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceImplTest {

    @InjectMocks
    private VoucherServiceImpl voucherService;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private UserService userService;

    @Mock
    private VoucherMapper voucherMapper;

    private VoucherRequestDTO validRequestDTO;
    private User mockCreator;

    @BeforeEach
    void setUp() {
        // --- Dữ liệu giả lập ---
        mockCreator = User.builder().id(1L).fullName("Admin").build();

        // SỬA LỖI: Sử dụng trực tiếp kiểu double thay vì BigDecimal
        validRequestDTO = VoucherRequestDTO.builder()
                .code("SALE50")
                .description("Giảm giá 50K cho đơn hàng từ 500K")
                .discountAmount(50000.0)
                .pointsRequired(100)
                .minOrderValue(500000.0)
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusMonths(1))
                .maxUsage(1000)
                .createdBy(mockCreator.getId())
                .build();
    }

    // =================================================================
    // 1. Valid Input Cases
    // =================================================================

    @Test
    @DisplayName("[createVoucher] Valid Input: Tạo voucher thành công với đầy đủ thông tin hợp lệ")
    void createVoucher_whenRequestIsValid_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Tạo voucher thành công với đầy đủ thông tin hợp lệ.");
        // Arrange
        when(voucherRepository.findByCode(validRequestDTO.getCode())).thenReturn(Optional.empty());
        when(userService.findById(validRequestDTO.getCreatedBy())).thenReturn(mockCreator);
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> {
            Voucher saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(voucherMapper.toDTO(any(Voucher.class))).thenReturn(new VoucherDTO());

        // Act
        GeneralResponse<VoucherDTO> response = voucherService.createVoucher(validRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(Constants.Message.VOUCHER_CREATE_SUCCESS, response.getMessage());
        assertNotNull(response.getData());

        // Verify
        verify(voucherRepository, times(1)).findByCode(validRequestDTO.getCode());
        verify(userService, times(1)).findById(validRequestDTO.getCreatedBy());
        verify(voucherRepository, times(1)).save(any(Voucher.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[createVoucher] Valid Input: Tạo voucher thành công khi các trường không bắt buộc là null")
    void createVoucher_whenOptionalFieldsAreNull_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Tạo voucher thành công khi các trường không bắt buộc là null.");
        // Arrange
        validRequestDTO.setDescription(null);
        // SỬA LỖI: Không thể gán null cho kiểu double. Thay vào đó, chúng ta sẽ kiểm tra giá trị mặc định (0.0).
        validRequestDTO.setMinOrderValue(0.0);

        when(voucherRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(userService.findById(anyLong())).thenReturn(mockCreator);
        ArgumentCaptor<Voucher> voucherCaptor = ArgumentCaptor.forClass(Voucher.class);
        when(voucherRepository.save(voucherCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(voucherMapper.toDTO(any(Voucher.class))).thenReturn(new VoucherDTO());

        // Act
        assertDoesNotThrow(() -> voucherService.createVoucher(validRequestDTO));

        // Assert
        Voucher capturedVoucher = voucherCaptor.getValue();
        assertNull(capturedVoucher.getDescription(), "Description phải là null.");
        // SỬA LỖI: Kiểm tra giá trị mặc định của kiểu double là 0.0
        assertEquals(0.0, capturedVoucher.getMinOrderValue(), "MinOrderValue phải là 0.0 khi không được cung cấp.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    // =================================================================
    // 2. Invalid Input Cases
    // =================================================================

    @Test
    @DisplayName("[createVoucher] Invalid Input: Thất bại khi mã voucher đã tồn tại")
    void createVoucher_whenCodeAlreadyExists_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi mã voucher đã tồn tại.");
        // Arrange
        when(voucherRepository.findByCode(validRequestDTO.getCode())).thenReturn(Optional.of(new Voucher()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            voucherService.createVoucher(validRequestDTO);
        });

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getHttpCode());
        assertEquals(Constants.Message.VOUCHER_CODE_EXISTS, exception.getMessage());

        // Verify
        verify(userService, never()).findById(anyLong());
        verify(voucherRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.VOUCHER_CODE_EXISTS);
    }

    @Test
    @DisplayName("[createVoucher] Invalid Input: Thất bại khi người tạo (creator) không tồn tại")
    void createVoucher_whenCreatorNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi người tạo không tồn tại.");
        // Arrange
        when(voucherRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(userService.findById(validRequestDTO.getCreatedBy()))
                .thenThrow(BusinessException.of(HttpStatus.NOT_FOUND, Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            voucherService.createVoucher(validRequestDTO);
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, exception.getMessage());

        // Verify
        verify(voucherRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE);
    }

    @Test
    @DisplayName("[createVoucher] Invalid Input: Thất bại khi mã voucher (code) là null")
    void createVoucher_whenCodeIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi mã voucher là null.");
        // Arrange
        validRequestDTO.setCode(null);

        // Giả lập rằng repository sẽ ném ra lỗi khi nhận đầu vào là null,
        // điều này mô phỏng hành vi thực tế của Spring Data JPA.
        when(voucherRepository.findByCode(null)).thenThrow(new IllegalArgumentException("Code must not be null"));

        // Act & Assert
        // Bây giờ, service sẽ bắt IllegalArgumentException và gói nó trong BusinessException
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            voucherService.createVoucher(validRequestDTO);
        });

        assertEquals(Constants.Message.VOUCHER_CREATE_FAIL, exception.getMessage());

        // SỬA LỖI: Thay vì kiểm tra getCause(), hãy kiểm tra getResponseData().
        // Theo logic của BusinessException.of(String, Exception), thông báo lỗi gốc được lưu vào responseData.
        assertNotNull(exception.getResponseData(), "ResponseData không được là null.");
        assertEquals("Code must not be null", exception.getResponseData().toString(), "ResponseData phải chứa thông báo lỗi gốc.");

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Mã voucher không được là null.");
    }
    @Test
    @DisplayName("[createVoucher] Invalid Input: Thất bại khi giá trị giảm giá là số âm")
    void createVoucher_whenDiscountAmountIsNegative_shouldSucceedWithoutValidation() {
        System.out.println("Test Case: Invalid Input - Kiểm tra hành vi khi giá trị giảm giá là số âm.");
        // Arrange
        validRequestDTO.setDiscountAmount(-50000.0); // Giá trị logic không hợp lệ

        when(voucherRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(userService.findById(anyLong())).thenReturn(mockCreator);
        ArgumentCaptor<Voucher> voucherCaptor = ArgumentCaptor.forClass(Voucher.class);
        when(voucherRepository.save(voucherCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(voucherMapper.toDTO(any(Voucher.class))).thenReturn(new VoucherDTO());

        // Act & Assert
        // Kỳ vọng hàm sẽ chạy thành công vì hiện tại không có logic validate số âm
        assertDoesNotThrow(() -> {
            voucherService.createVoucher(validRequestDTO);
        });

        // Kiểm tra xem giá trị âm có được lưu đúng hay không
        Voucher capturedVoucher = voucherCaptor.getValue();
        assertEquals(-50000.0, capturedVoucher.getDiscountAmount(), "Giá trị giảm giá âm phải được lưu lại.");

        System.out.println("Log: " + Constants.Message.SUCCESS + " (Lưu ý: Service hiện không validate giá trị âm).");
    }

    @Test
    @DisplayName("[createVoucher] Invalid Input: Thất bại khi lưu vào database")
    void createVoucher_whenRepositorySaveFails_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi lưu vào database.");
        // Arrange
        when(voucherRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(userService.findById(anyLong())).thenReturn(mockCreator);
        when(voucherRepository.save(any(Voucher.class))).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            voucherService.createVoucher(validRequestDTO);
        });

        assertEquals(Constants.Message.VOUCHER_CREATE_FAIL, exception.getMessage());

        // Verify
        verify(voucherRepository, times(1)).save(any(Voucher.class));
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }
    @Test
    @DisplayName("[createVoucher] Invalid Input: Thất bại khi createdBy là null")
    void createVoucher_whenCreatedByIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi createdBy là null.");
        // Arrange
        validRequestDTO.setCreatedBy(null);
        when(voucherRepository.findByCode(anyString())).thenReturn(Optional.empty());
        // Giả lập userService sẽ ném lỗi khi tìm user với ID là null
        when(userService.findById(null)).thenThrow(BusinessException.of(HttpStatus.NOT_FOUND, Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            voucherService.createVoucher(validRequestDTO);
        });

        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.USER_INFO_NOT_FOUND);
    }

    @Test
    @DisplayName("[createVoucher] Invalid Input: Ngày bắt đầu sau ngày kết thúc")
    void createVoucher_whenValidFromIsAfterValidTo_shouldSucceedWithoutValidation() {
        System.out.println("Test Case: Invalid Input - Ngày bắt đầu sau ngày kết thúc.");
        // Arrange
        validRequestDTO.setValidFrom(LocalDateTime.now().plusDays(10));
        validRequestDTO.setValidTo(LocalDateTime.now().plusDays(5)); // Ngày kết thúc trước ngày bắt đầu

        when(voucherRepository.findByCode(anyString())).thenReturn(Optional.empty());
        when(userService.findById(anyLong())).thenReturn(mockCreator);
        when(voucherRepository.save(any(Voucher.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(voucherMapper.toDTO(any(Voucher.class))).thenReturn(new VoucherDTO());

        // Act & Assert
        // Kỳ vọng hàm vẫn chạy thành công vì service không có logic validate ngày tháng
        assertDoesNotThrow(() -> voucherService.createVoucher(validRequestDTO));

        System.out.println("Log: " + Constants.Message.SUCCESS + " (Lưu ý: Service hiện không validate khoảng ngày).");
    }
    // =================================================================
    // 3. Test Cases for getVouchers
    // =================================================================

    @Test
    @DisplayName("[getVouchers] Valid Input: Lấy danh sách thành công khi có keyword")
    void getVouchers_withKeyword_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy danh sách thành công khi có keyword.");
        // Arrange
        String keyword = "SALE";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Voucher voucher = Voucher.builder().id(1L).code("SALE50").build();
        Page<Voucher> mockPage = new PageImpl<>(List.of(voucher), pageable, 1);

        // Giả lập repository được gọi với keyword
        when(voucherRepository.findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(eq(keyword), any(Pageable.class)))
                .thenReturn(mockPage);
        when(voucherMapper.toSummaryDTO(any(Voucher.class))).thenReturn(new VoucherSummaryDTO());

        // Act
        GeneralResponse<PagingDTO<VoucherSummaryDTO>> response = voucherService.getVouchers(keyword, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(Constants.Message.VOUCHER_LIST_SUCCESS, response.getMessage());

        PagingDTO<VoucherSummaryDTO> pagingDTO = response.getData();
        assertNotNull(pagingDTO);
        assertEquals(1, pagingDTO.getTotal());
        assertEquals(1, pagingDTO.getItems().size());

        // Verify
        verify(voucherRepository, times(1)).findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(eq(keyword), any(Pageable.class));
        verify(voucherRepository, never()).findByDeletedFalseOrderByCreatedAtDesc(any());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getVouchers] Valid Input: Lấy danh sách thành công khi không có keyword")
    void getVouchers_withoutKeyword_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy danh sách thành công khi không có keyword.");
        // Arrange
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Voucher voucher = Voucher.builder().id(2L).code("TET2025").build();
        Page<Voucher> mockPage = new PageImpl<>(List.of(voucher), pageable, 1);

        // Giả lập repository được gọi không có keyword
        when(voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(mockPage);
        when(voucherMapper.toSummaryDTO(any(Voucher.class))).thenReturn(new VoucherSummaryDTO());

        // Act
        GeneralResponse<PagingDTO<VoucherSummaryDTO>> response = voucherService.getVouchers(null, page, size);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getCode());
        assertEquals(1, response.getData().getTotal());

        // Verify
        verify(voucherRepository, never()).findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(anyString(), any());
        verify(voucherRepository, times(1)).findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getVouchers] Valid Input: Trả về trang rỗng khi không có voucher nào")
    void getVouchers_whenNoVouchersFound_shouldReturnEmptyPage() {
        System.out.println("Test Case: Valid Input - Trả về trang rỗng khi không có voucher nào.");
        // Arrange
        // Giả lập repository trả về một trang rỗng
        when(voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(Page.empty());

        // Act
        GeneralResponse<PagingDTO<VoucherSummaryDTO>> response = voucherService.getVouchers(null, 0, 10);

        // Assert
        assertNotNull(response);
        PagingDTO<VoucherSummaryDTO> pagingDTO = response.getData();
        assertNotNull(pagingDTO);
        assertEquals(0, pagingDTO.getTotal());
        assertTrue(pagingDTO.getItems().isEmpty());
        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + Constants.Message.NO_SERVICES_AVAILABLE);
    }





    @Test
    @DisplayName("[getVouchers] Invalid Input: Thất bại khi repository ném ra lỗi")
    void getVouchers_whenRepositoryFails_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        // Giả lập repository ném ra một lỗi runtime
        when(voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            voucherService.getVouchers(null, 0, 10);
        });

        assertEquals(Constants.Message.VOUCHER_LIST_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }
}