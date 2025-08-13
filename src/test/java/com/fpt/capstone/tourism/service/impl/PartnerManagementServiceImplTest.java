package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.PartnerSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.mapper.PartnerMapper;
import com.fpt.capstone.tourism.model.partner.Partner;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerRepository;
import com.fpt.capstone.tourism.repository.partner.ServiceTypeRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.fpt.capstone.tourism.dto.common.ServiceTypeShortDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.response.PartnerDetailDTO;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.partner.ServiceType;
import org.mockito.ArgumentMatchers;
import com.fpt.capstone.tourism.dto.request.PartnerUpdateRequestDTO;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyInt;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartnerManagementServiceImplTest {

    @InjectMocks
    private PartnerManagementServiceImpl partnerManagementService;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private PartnerMapper partnerMapper;

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private ServiceTypeRepository serviceTypeRepository;
    @Mock
    private LocationMapper locationMapper;
    private List<Partner> mockPartners;
    private PartnerSummaryDTO mockPartnerSummaryDTO;

    @BeforeEach
    void setUp() {
        // Setup mock data to be returned by the repository
        mockPartners = List.of(
                Partner.builder().id(1L).name("Vinpearl Hotel").build(),
                Partner.builder().id(2L).name("Saigon Tourist").build()
        );
        mockPartnerSummaryDTO = PartnerSummaryDTO.builder().id(1L).name("Vinpearl Hotel").build();
    }

    // --- Valid Input Cases ---

    @Test
    @DisplayName("[getPartners] Valid Input: Basic request with valid pagination and filters")
    void getPartners_whenAllInputsAreValid_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Basic request with valid pagination and filters.");
        // Arrange
        Page<Partner> partnerPage = new PageImpl<>(mockPartners);
        when(partnerRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(partnerPage);
        when(partnerMapper.toSummaryDTO(any(Partner.class))).thenReturn(mockPartnerSummaryDTO);

        // Act
        GeneralResponse<PagingDTO<PartnerSummaryDTO>> response = partnerManagementService.getPartners(0, 10, "vinpearl", false, "id", "asc");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode()); // SỬA Ở ĐÂY: getHttpCode() -> getCode()
        assertEquals(Constants.Message.PARTNER_LIST_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().getTotal());
        System.out.println("Log: " + Constants.Message.PARTNER_LIST_SUCCESS);
    }

    @Test
    @DisplayName("[getPartners] Valid Input: Request with null keyword and isDeleted")
    void getPartners_whenFiltersAreNull_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Request with null keyword and isDeleted.");
        // Arrange
        Page<Partner> partnerPage = new PageImpl<>(mockPartners);
        when(partnerRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(partnerPage);
        when(partnerMapper.toSummaryDTO(any(Partner.class))).thenReturn(mockPartnerSummaryDTO);

        // Act
        var response = partnerManagementService.getPartners(0, 10, null, null, "id", "asc");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode()); // SỬA Ở ĐÂY: getHttpCode() -> getCode()
        assertEquals(Constants.Message.PARTNER_LIST_SUCCESS, response.getMessage());
        assertEquals(2, response.getData().getTotal());
        System.out.println("Log: " + Constants.Message.PARTNER_LIST_SUCCESS);
    }

    @Test
    @DisplayName("[getPartners] Valid Input: Request with whitespace keyword")
    void getPartners_whenKeywordIsWhitespace_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Request with whitespace keyword.");
        // Arrange
        Page<Partner> partnerPage = new PageImpl<>(mockPartners);
        when(partnerRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(partnerPage);
        when(partnerMapper.toSummaryDTO(any(Partner.class))).thenReturn(mockPartnerSummaryDTO);

        // Act
        var response = partnerManagementService.getPartners(0, 10, "   ", null, "id", "asc");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode()); // SỬA Ở ĐÂY: getHttpCode() -> getCode()
        assertEquals(Constants.Message.PARTNER_LIST_SUCCESS, response.getMessage());
        assertEquals(2, response.getData().getTotal());
        System.out.println("Log: " + Constants.Message.PARTNER_LIST_SUCCESS);
    }

    // --- Invalid Input Cases ---

    @Test
    @DisplayName("[getPartners] Invalid Input: Negative page number")
    void getPartners_whenPageIsNegative_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Negative page number.");
        // Arrange
        int invalidPage = -1;

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.getPartners(invalidPage, 10, null, null, "id", "asc");
        });

        assertEquals(Constants.Message.PARTNER_LIST_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_LIST_FAIL);
    }

    @Test
    @DisplayName("[getPartners] Invalid Input: Zero size")
    void getPartners_whenSizeIsZero_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Zero size.");
        // Arrange
        int invalidSize = 0;

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.getPartners(0, invalidSize, null, null, "id", "asc");
        });

        assertEquals(Constants.Message.PARTNER_LIST_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_LIST_FAIL);
    }

    @Test
    @DisplayName("[getPartners] Invalid Input: Negative size")
    void getPartners_whenSizeIsNegative_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Negative size.");
        // Arrange
        int invalidSize = -5;

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.getPartners(0, invalidSize, null, null, "id", "asc");
        });

        assertEquals(Constants.Message.PARTNER_LIST_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_LIST_FAIL);
    }

    @Test
    @DisplayName("[getPartnerDetail] Valid Input: Lấy chi tiết đối tác thành công khi ID hợp lệ")
    void getPartnerDetail_whenIdIsValidAndPartnerExists_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Lấy chi tiết đối tác thành công.");
        // Arrange
        Long validId = 1L;
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();
        Partner mockPartner = Partner.builder()
                .id(validId)
                .name("Vinpearl Hotel")
                .location(mockLocation)
                .serviceType(mockServiceType)
                .build();

        // Giả lập repository tìm thấy đối tác
        when(partnerRepository.findById(anyInt())).thenReturn(Optional.of(mockPartner));
        // Giả lập repository trả về danh sách options
        when(locationRepository.findAllLocations()).thenReturn(Collections.singletonList(mockLocation));
        when(serviceTypeRepository.findAll()).thenReturn(Collections.singletonList(mockServiceType));
        // Giả lập mapper
        when(locationMapper.toLocationShortDTO(any(Location.class)))
                .thenReturn(LocationShortDTO.builder().id(10L).name("Hà Nội").build());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.getPartnerDetail(validId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_DETAIL_SUCCESS, response.getMessage());

        PartnerDetailDTO dto = response.getData();
        assertNotNull(dto);
        assertEquals(validId, dto.getId());
        assertEquals("Vinpearl Hotel", dto.getName());
        assertEquals("Hà Nội", dto.getLocation().getName());
        assertEquals("Khách sạn", dto.getServiceType().getName());
        assertFalse(dto.getLocationOptions().isEmpty(), "Phải có danh sách tùy chọn địa điểm.");
        assertFalse(dto.getServiceTypeOptions().isEmpty(), "Phải có danh sách tùy chọn loại dịch vụ.");

        System.out.println("Log: " + Constants.Message.PARTNER_DETAIL_SUCCESS);
    }

    @Test
    @DisplayName("[getPartnerDetail] Invalid Input: Thất bại khi không tìm thấy đối tác")
    void getPartnerDetail_whenPartnerNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Không tìm thấy đối tác.");
        // Arrange
        Long nonExistentId = 99L;
        // Giả lập repository không tìm thấy đối tác
        when(partnerRepository.findById(ArgumentMatchers.eq(Math.toIntExact(nonExistentId)))).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.getPartnerDetail(nonExistentId);
        });

        assertEquals(Constants.Message.SERVICE_PROVIDER_NOT_FOUND, exception.getMessage());
        System.out.println("Log: " + Constants.Message.SERVICE_PROVIDER_NOT_FOUND);
    }

    @Test
    @DisplayName("[getPartnerDetail] Invalid Input: Thất bại khi ID quá lớn (lỗi ArithmeticException)")
    void getPartnerDetail_whenIdIsTooLarge_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - ID quá lớn.");
        // Arrange
        Long largeId = (long) Integer.MAX_VALUE + 1;

        // Act & Assert
        // Lỗi ArithmeticException sẽ được bắt và chuyển thành BusinessException
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.getPartnerDetail(largeId);
        });

        assertEquals(Constants.Message.PARTNER_DETAIL_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_DETAIL_FAIL);
    }

    @Test
    @DisplayName("[getPartnerDetail] Invalid Input: Thất bại khi repository gặp lỗi")
    void getPartnerDetail_whenRepositoryThrowsException_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Repository gặp lỗi.");
        // Arrange
        Long validId = 1L;
        // Giả lập repository ném ra lỗi khi được gọi
        when(partnerRepository.findById(anyInt())).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.getPartnerDetail(validId);
        });

        assertEquals(Constants.Message.PARTNER_DETAIL_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_DETAIL_FAIL);
    }

    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công với dữ liệu hợp lệ")
    void addPartner_whenRequestIsValid_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công.");
        // Arrange
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác mới")
                .contactName("Người liên hệ A")
                .contactEmail("contact.a@example.com")
                .contactPhone("0123456789")
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        // Giả lập các repository tìm thấy entity
        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // Giả lập hàm save trả về chính đối tượng đã được gán ID
        when(partnerRepository.save(any(Partner.class))).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(999L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.singletonList(mockLocation));
        when(serviceTypeRepository.findAll()).thenReturn(Collections.singletonList(mockServiceType));
        when(locationMapper.toLocationShortDTO(any(Location.class)))
                .thenReturn(LocationShortDTO.builder().id(10L).name("Hà Nội").build());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        PartnerDetailDTO dto = response.getData();
        assertNotNull(dto);
        assertEquals(999L, dto.getId());
        assertEquals("Đối tác mới", dto.getName());
        assertEquals("Hà Nội", dto.getLocation().getName());

        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("[addPartner] Invalid Input: Thất bại khi không tìm thấy Location")
    void addPartner_whenLocationNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Không tìm thấy Location.");
        // Arrange
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác lỗi")
                .locationId(99L) // ID không tồn tại
                .serviceTypeId(100L)
                .build();

        // Giả lập locationRepository không tìm thấy
        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.addPartner(requestDTO);
        });

        assertEquals(Constants.Message.LOCATION_NOT_FOUND, exception.getMessage());
        System.out.println("Log: " + Constants.Message.LOCATION_NOT_FOUND);

        // Verify
        verify(partnerRepository, never()).save(any());
    }

    @Test
    @DisplayName("[addPartner] Invalid Input: Thất bại khi không tìm thấy ServiceType")
    void addPartner_whenServiceTypeNotFound_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Không tìm thấy ServiceType.");
        // Arrange
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác lỗi")
                .locationId(10L)
                .serviceTypeId(999L) // ID không tồn tại
                .build();

        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();

        // Giả lập locationRepository tìm thấy, nhưng serviceTypeRepository không tìm thấy
        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.addPartner(requestDTO);
        });

        assertEquals(Constants.Message.SERVICE_TYPE_NOT_FOUND, exception.getMessage());
        System.out.println("Log: " + Constants.Message.SERVICE_TYPE_NOT_FOUND);

        // Verify
        verify(partnerRepository, never()).save(any());
    }

    @Test
    @DisplayName("[addPartner] Invalid Input: Thất bại khi repository gặp lỗi lúc lưu")
    void addPartner_whenRepositoryThrowsExceptionOnSave_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Repository gặp lỗi khi lưu.");
        // Arrange
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác mới")
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // Giả lập repository ném ra lỗi khi được gọi
        when(partnerRepository.save(any(Partner.class))).thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.addPartner(requestDTO);
        });

        assertEquals(Constants.Message.PARTNER_ADD_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_FAIL);

        // Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }
    @Test
    @DisplayName("[addPartner] Invalid Input: Thất bại khi tên đối tác (name) là null")
    void addPartner_whenNameIsNull_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi tên đối tác là null.");
        // Arrange
        // 1. Tạo request với name là null
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name(null) // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường để code chạy đến bước save
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Giả lập lỗi từ database khi cố gắng lưu một đối tượng có trường 'name' (not-null) là null.
        // DataIntegrityViolationException là một ví dụ điển hình cho lỗi ràng buộc not-null.
        when(partnerRepository.save(any(Partner.class))).thenThrow(new org.springframework.dao.DataIntegrityViolationException("constraint [partners.name]"));

        // Act & Assert
        // 4. Kỳ vọng một BusinessException được ném ra từ khối catch chung
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.addPartner(requestDTO);
        });

        // 5. Kiểm tra thông báo lỗi
        assertEquals(Constants.Message.PARTNER_ADD_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_FAIL + ". Nguyên nhân: Tên đối tác không được để trống.");

        // 6. Verify rằng hàm save đã được gọi, nhưng sau đó gây ra lỗi
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }
    @Test
    @DisplayName("[addPartner] Invalid Input: Thất bại khi tên đối tác (name) chỉ chứa khoảng trắng")
    void addPartner_whenNameIsWhitespace_shouldThrowBusinessException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi tên đối tác chỉ chứa khoảng trắng.");
        // Arrange
        // 1. Tạo request với name chỉ chứa khoảng trắng
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("   ") // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Giả lập lỗi từ database khi cố gắng lưu một đối tượng có trường 'name' không hợp lệ.
        when(partnerRepository.save(any(Partner.class))).thenThrow(new org.springframework.dao.DataIntegrityViolationException("constraint [partners.name]"));

        // Act & Assert
        // 4. Kỳ vọng một BusinessException được ném ra
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            partnerManagementService.addPartner(requestDTO);
        });

        // 5. Kiểm tra thông báo lỗi
        assertEquals(Constants.Message.PARTNER_ADD_FAIL, exception.getMessage());
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_FAIL + ". Nguyên nhân: Tên đối tác không hợp lệ (chỉ chứa khoảng trắng).");

        // 6. Verify rằng hàm save đã được gọi, nhưng sau đó gây ra lỗi
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }
    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi tên người liên hệ (contactName) là null")
    void addPartner_whenContactNameIsNull_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactName là null.");
        // Arrange
        // 1. Tạo request với contactName là null
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác không có người liên hệ")
                .contactName(null) // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1001L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertNull(capturedPartner.getContactName(), "Tên người liên hệ trong đối tượng đã lưu phải là null.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi tên người liên hệ (contactName) chỉ chứa khoảng trắng")
    void addPartner_whenContactNameIsWhitespace_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactName chỉ chứa khoảng trắng.");
        // Arrange
        // 1. Tạo request với contactName chỉ chứa khoảng trắng
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác có tên liên hệ lạ")
                .contactName("   ") // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1002L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertEquals("   ", capturedPartner.getContactName(), "Tên người liên hệ phải được lưu đúng như giá trị khoảng trắng đã truyền vào.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi email liên hệ (contactEmail) chỉ chứa khoảng trắng")
    void addPartner_whenContactEmailIsWhitespace_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactEmail chỉ chứa khoảng trắng.");
        // Arrange
        // 1. Tạo request với contactEmail chỉ chứa khoảng trắng
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác có email lạ")
                .contactEmail("   ") // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1004L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertEquals("   ", capturedPartner.getContactEmail(), "Email liên hệ phải được lưu đúng như giá trị khoảng trắng đã truyền vào.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }
    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi SĐT liên hệ (contactPhone) là null")
    void addPartner_whenContactPhoneIsNull_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactPhone là null.");
        // Arrange
        // 1. Tạo request với contactPhone là null
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác không có SĐT")
                .contactPhone(null) // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1005L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertNull(capturedPartner.getContactPhone(), "SĐT liên hệ trong đối tượng đã lưu phải là null.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi SĐT liên hệ (contactPhone) chỉ chứa khoảng trắng")
    void addPartner_whenContactPhoneIsWhitespace_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactPhone chỉ chứa khoảng trắng.");
        // Arrange
        // 1. Tạo request với contactPhone chỉ chứa khoảng trắng
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác có SĐT lạ")
                .contactPhone("   ") // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1006L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertEquals("   ", capturedPartner.getContactPhone(), "SĐT liên hệ phải được lưu đúng như giá trị khoảng trắng đã truyền vào.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi SĐT liên hệ (contactPhone) có định dạng không hợp lệ")
    void addPartner_whenContactPhoneIsInvalidFormat_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactPhone có định dạng không hợp lệ.");
        // Arrange
        // 1. Tạo request với contactPhone có định dạng không hợp lệ
        String invalidPhone = "Đây không phải là số điện thoại";
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác có SĐT không hợp lệ")
                .contactPhone(invalidPhone) // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1007L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertEquals(invalidPhone, capturedPartner.getContactPhone(), "SĐT liên hệ phải được lưu đúng như giá trị không hợp lệ đã truyền vào.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }
    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi email liên hệ (contactEmail) có định dạng không hợp lệ")
    void addPartner_whenContactEmailIsInvalidFormat_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactEmail có định dạng không hợp lệ.");
        // Arrange
        // 1. Tạo request với contactEmail có định dạng không hợp lệ
        String invalidEmail = "day-khong-phai-la-email";
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác có email không hợp lệ")
                .contactEmail(invalidEmail) // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1008L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertEquals(invalidEmail, capturedPartner.getContactEmail(), "Email liên hệ phải được lưu đúng như giá trị không hợp lệ đã truyền vào.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("[addPartner] Valid Input: Thêm đối tác thành công khi email liên hệ (contactEmail) là null")
    void addPartner_whenContactEmailIsNull_shouldSucceed() {
        System.out.println("Test Case: Valid Input - Thêm đối tác thành công khi contactEmail là null.");
        // Arrange
        // 1. Tạo request với contactEmail là null
        PartnerUpdateRequestDTO requestDTO = PartnerUpdateRequestDTO.builder()
                .name("Đối tác không có email")
                .contactEmail(null) // Trường hợp cần test
                .locationId(10L)
                .serviceTypeId(100L)
                .build();

        // 2. Giả lập các dependency khác hoạt động bình thường
        Location mockLocation = Location.builder().id(10L).name("Hà Nội").build();
        ServiceType mockServiceType = ServiceType.builder().id(100L).code("HOTEL").name("Khách sạn").build();

        when(locationRepository.findById(requestDTO.getLocationId())).thenReturn(Optional.of(mockLocation));
        when(serviceTypeRepository.findById(requestDTO.getServiceTypeId())).thenReturn(Optional.of(mockServiceType));

        // 3. Sử dụng ArgumentCaptor để bắt lại đối tượng Partner được lưu
        ArgumentCaptor<Partner> partnerCaptor = ArgumentCaptor.forClass(Partner.class);
        when(partnerRepository.save(partnerCaptor.capture())).thenAnswer(invocation -> {
            Partner partnerToSave = invocation.getArgument(0);
            partnerToSave.setId(1003L); // Giả lập ID sau khi lưu
            return partnerToSave;
        });

        // Giả lập cho phần buildDetailDTO
        when(locationRepository.findAllLocations()).thenReturn(Collections.emptyList());
        when(serviceTypeRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        GeneralResponse<PartnerDetailDTO> response = partnerManagementService.addPartner(requestDTO);

        // Assert
        // 4. Kiểm tra kết quả trả về
        assertEquals(200, response.getCode());
        assertEquals(Constants.Message.PARTNER_ADD_SUCCESS, response.getMessage());

        // 5. Kiểm tra đối tượng đã được "bắt"
        Partner capturedPartner = partnerCaptor.getValue();
        assertNull(capturedPartner.getContactEmail(), "Email liên hệ trong đối tượng đã lưu phải là null.");
        System.out.println("Log: " + Constants.Message.PARTNER_ADD_SUCCESS);

        // 6. Verify
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }
}