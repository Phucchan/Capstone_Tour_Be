package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.PendingServiceUpdateDTO;
import com.fpt.capstone.tourism.dto.response.ServiceInfoDTO;
import com.fpt.capstone.tourism.mapper.partner.ServiceInfoMapper;
import com.fpt.capstone.tourism.model.enums.CostType;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;
import com.fpt.capstone.tourism.model.partner.Partner;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import com.fpt.capstone.tourism.repository.partner.PartnerServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceApprovalServiceImplTest {

    @InjectMocks
    private PartnerServiceApprovalServiceImpl partnerServiceApprovalService;

    @Mock
    private PartnerServiceRepository partnerServiceRepository;

    @Mock
    private ServiceInfoMapper serviceInfoMapper;

    private Page<PartnerService> mockServicePage;
    private ServiceInfoDTO mockServiceInfoDTO;

    @BeforeEach
    void setUp() {
        PartnerService service1 = PartnerService.builder().id(1L).name("Hotel A - Pending").build();
        PartnerService service2 = PartnerService.builder().id(2L).name("Restaurant B - Pending").build();
        List<PartnerService> services = List.of(service1, service2);
        // Giả lập một trang kết quả trả về từ repository
        mockServicePage = new PageImpl<>(services, PageRequest.of(0, 10), services.size());

        // Giả lập kết quả từ mapper
        mockServiceInfoDTO = ServiceInfoDTO.builder().id(1L).name("Hotel A - Pending").build();
    }

    // =================================================================
    // 1. Test Cases for getPendingServices
    // =================================================================

    @Test
    @DisplayName("[getPendingServices] Normal Case: Lấy danh sách thành công với keyword hợp lệ")
    void getPendingServices_whenKeywordIsValid_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy danh sách thành công với keyword hợp lệ.");
        // Arrange
        int page = 0;
        int size = 10;
        String keyword = "hotel";

        // Giả lập repository được gọi với keyword
        when(partnerServiceRepository.findByStatusAndNameContainingIgnoreCase(
                eq(PartnerServiceStatus.PENDING), eq(keyword), any(Pageable.class)))
                .thenReturn(mockServicePage);
        when(serviceInfoMapper.toDto(any(PartnerService.class))).thenReturn(mockServiceInfoDTO);

        // Act
        GeneralResponse<PagingDTO<ServiceInfoDTO>> response = partnerServiceApprovalService.getPendingServices(page, size, keyword);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());

        PagingDTO<ServiceInfoDTO> pagingDTO = response.getData();
        assertNotNull(pagingDTO);
        assertEquals(mockServicePage.getTotalElements(), pagingDTO.getTotal());
        assertEquals(2, pagingDTO.getItems().size());

        // Verify
        verify(partnerServiceRepository, times(1)).findByStatusAndNameContainingIgnoreCase(any(), anyString(), any());
        verify(partnerServiceRepository, never()).findByStatus(any(), any());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getPendingServices] Normal Case: Lấy danh sách thành công khi keyword là null")
    void getPendingServices_whenKeywordIsNull_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy danh sách thành công khi keyword là null.");
        // Arrange
        int page = 0;
        int size = 10;
        String keyword = null;

        // Giả lập repository được gọi không có keyword
        when(partnerServiceRepository.findByStatus(eq(PartnerServiceStatus.PENDING), any(Pageable.class)))
                .thenReturn(mockServicePage);
        when(serviceInfoMapper.toDto(any(PartnerService.class))).thenReturn(mockServiceInfoDTO);

        // Act
        GeneralResponse<PagingDTO<ServiceInfoDTO>> response = partnerServiceApprovalService.getPendingServices(page, size, keyword);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals(2, response.getData().getTotal());

        // Verify
        verify(partnerServiceRepository, never()).findByStatusAndNameContainingIgnoreCase(any(), anyString(), any());
        verify(partnerServiceRepository, times(1)).findByStatus(any(), any());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getPendingServices] Normal Case: Lấy danh sách thành công khi keyword là khoảng trắng")
    void getPendingServices_whenKeywordIsWhitespace_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Lấy danh sách thành công khi keyword là khoảng trắng.");
        // Arrange
        int page = 0;
        int size = 10;
        String keyword = "   ";

        // Giả lập repository được gọi không có keyword (vì StringUtils.hasText("   ") là false)
        when(partnerServiceRepository.findByStatus(eq(PartnerServiceStatus.PENDING), any(Pageable.class)))
                .thenReturn(mockServicePage);

        // Act
        partnerServiceApprovalService.getPendingServices(page, size, keyword);

        // Assert & Verify
        verify(partnerServiceRepository, times(1)).findByStatus(any(), any());
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[getPendingServices] Normal Case: Trả về danh sách rỗng khi không có dịch vụ nào")
    void getPendingServices_whenNoServicesFound_shouldReturnEmptyPage() {
        System.out.println("Test Case: Normal Input - Trả về danh sách rỗng khi không có dịch vụ nào.");
        // Arrange
        // Giả lập repository trả về một trang rỗng
        when(partnerServiceRepository.findByStatus(any(), any(Pageable.class)))
                .thenReturn(Page.empty());

        // Act
        GeneralResponse<PagingDTO<ServiceInfoDTO>> response = partnerServiceApprovalService.getPendingServices(0, 10, null);

        // Assert
        assertNotNull(response);
        PagingDTO<ServiceInfoDTO> pagingDTO = response.getData();
        assertNotNull(pagingDTO);
        assertEquals(0, pagingDTO.getTotal());
        assertTrue(pagingDTO.getItems().isEmpty());
        System.out.println("Log: " + Constants.Message.SUCCESS + ". Không có dữ liệu để hiển thị.");
    }

    // =================================================================
    // 2. Abnormal Cases - Các trường hợp không hợp lệ
    // =================================================================

    @Test
    @DisplayName("[getPendingServices] Abnormal Case: Thất bại khi page là số âm")
    void getPendingServices_whenPageIsNegative_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi page là số âm.");
        // Arrange
        int invalidPage = -1;
        int size = 10;

        // Act & Assert
        // PageRequest.of() sẽ ném ra lỗi này
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            partnerServiceApprovalService.getPendingServices(invalidPage, size, null);
        });

        assertTrue(exception.getMessage().contains("Page index must not be less than zero"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Page không được là số âm.");
    }

    @Test
    @DisplayName("[getPendingServices] Abnormal Case: Thất bại khi size nhỏ hơn 1")
    void getPendingServices_whenSizeIsLessThanOne_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi size nhỏ hơn 1.");
        // Arrange
        int page = 0;
        int invalidSize = 0;

        // Act & Assert
        // PageRequest.of() sẽ ném ra lỗi này
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            partnerServiceApprovalService.getPendingServices(page, invalidSize, null);
        });

        assertTrue(exception.getMessage().contains("Page size must not be less than one"));
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Size phải lớn hơn 0.");
    }

    @Test
    @DisplayName("[getPendingServices] Abnormal Case: Thất bại khi repository ném ra lỗi")
    void getPendingServices_whenRepositoryThrowsException_shouldPropagateException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        // Giả lập repository ném ra một lỗi runtime khi được gọi
        when(partnerServiceRepository.findByStatus(any(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        // Kỳ vọng một RuntimeException sẽ được ném ra từ service vì không có khối try-catch
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            partnerServiceApprovalService.getPendingServices(0, 10, null);
        });

        assertEquals("Database connection error", exception.getMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Lỗi từ tầng repository.");
    }
    // =================================================================
    // 3. Test Cases for updateService
    // =================================================================

    @Test
    @DisplayName("[updateService] Normal Case: Cập nhật thành công tất cả các trường")
    void updateService_whenAllFieldsAreValid_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Cập nhật thành công tất cả các trường.");
        // Arrange
        Long serviceId = 1L;
        // 1. Dữ liệu gốc của service
        PartnerService existingService = PartnerService.builder()
                .id(serviceId)
                .name("Old Name")
                .status(PartnerServiceStatus.PENDING)
                .nettPrice(100.0)
                .partner(Partner.builder().id(99L).build()) // Giả lập partner ban đầu
                .build();

        // 2. Dữ liệu mới từ DTO
        PendingServiceUpdateDTO updateDTO = PendingServiceUpdateDTO.builder()
                .partnerId(2L)
                .imageUrl("http://new.image/url")
                .description("New Description")
                .nettPrice(150.0)
                .sellingPrice(200.0)
                .costType("PER_PERSON")
                // SỬA LỖI: Thay "APPROVED" bằng "ACTIVE" để khớp với enum hiện tại
                .newStatus("ACTIVE")
                .build();

        // 3. Giả lập repository tìm thấy service
        when(partnerServiceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));
        // Giả lập mapper
        when(serviceInfoMapper.toDto(any(PartnerService.class))).thenAnswer(invocation ->
                ServiceInfoDTO.builder().id(invocation.getArgument(0, PartnerService.class).getId()).build());

        // 4. Sử dụng ArgumentCaptor để bắt lại đối tượng được lưu
        ArgumentCaptor<PartnerService> serviceCaptor = ArgumentCaptor.forClass(PartnerService.class);
        when(partnerServiceRepository.save(serviceCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));


        // Act
        GeneralResponse<ServiceInfoDTO> response = partnerServiceApprovalService.updateService(serviceId, updateDTO);

        // Assert
        // 5. Kiểm tra response trả về
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());

        // 6. Kiểm tra đối tượng đã được "bắt"
        PartnerService capturedService = serviceCaptor.getValue();
        assertEquals(updateDTO.getPartnerId(), capturedService.getPartner().getId());
        assertEquals(updateDTO.getImageUrl(), capturedService.getImageUrl());
        assertEquals(updateDTO.getDescription(), capturedService.getDescription());
        assertEquals(updateDTO.getNettPrice(), capturedService.getNettPrice());
        assertEquals(updateDTO.getSellingPrice(), capturedService.getSellingPrice());
        assertEquals(CostType.PER_PERSON, capturedService.getCostType());
        // SỬA LỖI: Assertion phải khớp với giá trị enum được truyền vào
        assertEquals(PartnerServiceStatus.ACTIVE, capturedService.getStatus());

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[updateService] Normal Case: Cập nhật thành công chỉ trạng thái")
    void updateService_whenOnlyStatusIsUpdated_shouldSucceed() {
        System.out.println("Test Case: Normal Input - Cập nhật thành công chỉ trạng thái.");
        // Arrange
        Long serviceId = 1L;
        PartnerService existingService = PartnerService.builder()
                .id(serviceId)
                .name("Service Name")
                .status(PartnerServiceStatus.PENDING) // Trạng thái cũ
                .build();

        PendingServiceUpdateDTO updateDTO = PendingServiceUpdateDTO.builder()
                // SỬA LỖI: Thay "REJECTED" bằng "DEACTIVE" để khớp với enum hiện tại
                .newStatus("DEACTIVE") // Chỉ cập nhật trạng thái mới
                .build();

        when(partnerServiceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));
        ArgumentCaptor<PartnerService> serviceCaptor = ArgumentCaptor.forClass(PartnerService.class);
        when(partnerServiceRepository.save(serviceCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(serviceInfoMapper.toDto(any(PartnerService.class))).thenReturn(new ServiceInfoDTO());


        // Act
        partnerServiceApprovalService.updateService(serviceId, updateDTO);

        // Assert
        PartnerService capturedService = serviceCaptor.getValue();
        // SỬA LỖI: Assertion phải khớp với giá trị enum được truyền vào và thông báo lỗi
        assertEquals(PartnerServiceStatus.DEACTIVE, capturedService.getStatus(), "Trạng thái phải được cập nhật thành DEACTIVE.");
        assertEquals("Service Name", capturedService.getName(), "Tên dịch vụ không được thay đổi.");

        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @DisplayName("[updateService] Abnormal Case: Thất bại khi không tìm thấy dịch vụ")
    void updateService_whenServiceNotFound_shouldReturnNotFoundResponse() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi không tìm thấy dịch vụ.");
        // Arrange
        Long nonExistentId = 99L;
        PendingServiceUpdateDTO dto = new PendingServiceUpdateDTO();

        // Giả lập repository không tìm thấy service
        when(partnerServiceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        GeneralResponse<ServiceInfoDTO> response = partnerServiceApprovalService.updateService(nonExistentId, dto);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getCode());
        assertEquals("Service not found", response.getMessage());
        assertNull(response.getData());

        // Verify
        verify(partnerServiceRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.SERVICE_NOT_FOUND);
    }

    @Test
    @DisplayName("[updateService] Abnormal Case: Thất bại khi trạng thái (newStatus) không hợp lệ")
    void updateService_whenInvalidStatusString_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi newStatus không hợp lệ.");
        // Arrange
        Long serviceId = 1L;
        PartnerService existingService = PartnerService.builder().id(serviceId).status(PartnerServiceStatus.PENDING).build();
        PendingServiceUpdateDTO updateDTO = PendingServiceUpdateDTO.builder()
                .newStatus("INVALID_STATUS_VALUE") // Giá trị không hợp lệ
                .build();

        when(partnerServiceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));

        // Act & Assert
        // Kỳ vọng một IllegalArgumentException vì `PartnerServiceStatus.valueOf()` sẽ thất bại
        assertThrows(IllegalArgumentException.class, () -> {
            partnerServiceApprovalService.updateService(serviceId, updateDTO);
        });

        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: Giá trị trạng thái không hợp lệ.");
    }

    @Test
    @DisplayName("[updateService] Abnormal Case: Thất bại khi repository gặp lỗi lúc lưu")
    void updateService_whenRepositoryFailsOnSave_shouldThrowException() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi repository gặp lỗi lúc lưu.");
        // Arrange
        Long serviceId = 1L;
        PartnerService existingService = PartnerService.builder().id(serviceId).build();
        PendingServiceUpdateDTO updateDTO = PendingServiceUpdateDTO.builder().description("New Desc").build();

        when(partnerServiceRepository.findById(serviceId)).thenReturn(Optional.of(existingService));
        // Giả lập hàm save ném ra lỗi
        when(partnerServiceRepository.save(any(PartnerService.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        // Kỳ vọng exception từ repository sẽ được ném ra ngoài
        assertThrows(RuntimeException.class, () -> {
            partnerServiceApprovalService.updateService(serviceId, updateDTO);
        });

        System.out.println("Log: " + Constants.Message.GENERAL_FAIL_MESSAGE);
    }
}