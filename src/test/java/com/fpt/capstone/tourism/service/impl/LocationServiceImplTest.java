package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.LocationRequestDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.service.S3Service;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private S3Service s3Service; // Mock S3Service

    @InjectMocks
    private LocationServiceImpl locationService;

    private Location location;
    private LocationDTO locationDTO;
    private LocationRequestDTO locationRequestDTO;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Common test data
        location = new Location();
        location.setId(1L);
        location.setName("Hà Nội");
        location.setImage("http://bucket.url/locations/hanoi.jpg");
        location.setDeleted(false);

        locationDTO = new LocationDTO();
        locationDTO.setId(1L);
        locationDTO.setName("Hà Nội");
        locationDTO.setImage("http://bucket.url/locations/hanoi.jpg");
        locationDTO.setDeleted(false);

        locationRequestDTO = new LocationRequestDTO();
        locationRequestDTO.setName("Hà Nội");
        locationRequestDTO.setDescription("Thủ đô Việt Nam");

        // Mock file for upload tests
        mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
    }

    // =================================================================
    // 1. Test Cases for saveLocation
    // =================================================================

    @Test
    @Order(1)
    @DisplayName("[saveLocation] Success Case: Tạo địa điểm thành công với dữ liệu hợp lệ")
    void saveLocation_Success() {
        System.out.println("Test Case: Valid Input - Tạo địa điểm thành công.");
        // Arrange
        when(locationRepository.findByName("Hà Nội")).thenReturn(null);
        when(s3Service.uploadFile(any(MultipartFile.class), eq("locations"))).thenReturn("locations/new-image.jpg");
        when(locationMapper.toEntity(locationRequestDTO)).thenReturn(location);
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        when(locationMapper.toDTO(any(Location.class))).thenReturn(locationDTO);

        // Act
        GeneralResponse<LocationDTO> response = locationService.saveLocation(locationRequestDTO, mockFile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.CREATE_LOCATION_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertEquals("Hà Nội", response.getData().getName());

        // Verify
        verify(s3Service, times(1)).uploadFile(mockFile, "locations");
        verify(locationRepository, times(1)).save(any(Location.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @Order(2)
    @DisplayName("[saveLocation] Abnormal Case: Thất bại khi tên địa điểm đã tồn tại")
    void saveLocation_ShouldThrowException_WhenNameIsDuplicate() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi tên địa điểm đã tồn tại.");
        // Arrange
        when(locationRepository.findByName("Hà Nội")).thenReturn(location);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            locationService.saveLocation(locationRequestDTO, mockFile);
        });
        assertEquals(Constants.Message.EXISTED_LOCATION, exception.getResponseMessage());

        // Verify
        verify(s3Service, never()).uploadFile(any(), anyString());
        verify(locationRepository, never()).save(any());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.EXISTED_LOCATION);
    }

    @Test
    @Order(3)
    @DisplayName("[saveLocation] Abnormal Case: Thất bại khi file ảnh là null")
    void saveLocation_ShouldThrowException_WhenFileIsNull() {
        System.out.println("Test Case: Abnormal Input - Thất bại khi file ảnh là null.");
        // Arrange
        MultipartFile nullFile = null;

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            locationService.saveLocation(locationRequestDTO, nullFile);
        });
        assertEquals(Constants.Message.EMPTY_LOCATION_IMAGE, exception.getResponseMessage());
        System.out.println("Log: " + Constants.Message.FAILED + ". Nguyên nhân: " + Constants.Message.EMPTY_LOCATION_IMAGE);
    }

    // =================================================================
    // 2. Test Cases for updateLocation
    // =================================================================

    @Test
    @Order(4)
    @DisplayName("[updateLocation] Success Case: Cập nhật thành công với file ảnh mới")
    void updateLocation_Success_WithNewFile() {
        System.out.println("Test Case: Valid Input - Cập nhật địa điểm thành công với file ảnh mới.");
        // Arrange
        Long locationId = 1L;
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(s3Service.uploadFile(any(MultipartFile.class), eq("locations"))).thenReturn("locations/updated-image.jpg");
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        when(locationMapper.toDTO(any(Location.class))).thenReturn(locationDTO);

        // Act
        GeneralResponse<LocationDTO> response = locationService.updateLocation(locationId, locationRequestDTO, mockFile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.GENERAL_SUCCESS_MESSAGE, response.getMessage());

        // Verify
        verify(s3Service, times(1)).uploadFile(mockFile, "locations");
        verify(locationRepository, times(1)).save(any(Location.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    @Test
    @Order(5)
    @DisplayName("[updateLocation] Success Case: Cập nhật thành công khi không có file ảnh mới (file là null)")
    void updateLocation_Success_WithoutNewFile() {
        System.out.println("Test Case: Valid Input - Cập nhật địa điểm thành công không cần file ảnh mới.");
        // Arrange
        Long locationId = 1L;
        MultipartFile nullFile = null;
        when(locationRepository.findById(locationId)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);
        when(locationMapper.toDTO(any(Location.class))).thenReturn(locationDTO);

        // Act
        GeneralResponse<LocationDTO> response = locationService.updateLocation(locationId, locationRequestDTO, nullFile);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        // Verify
        verify(s3Service, never()).uploadFile(any(), anyString());
        verify(locationRepository, times(1)).save(any(Location.class));
        System.out.println("Log: " + Constants.Message.SUCCESS);
    }

    // =================================================================
    // 3. Test Cases for getAllDepartures
    // =================================================================

    @Test
    @Order(6)
    @DisplayName("[getAllDepartures] Valid Case: Lấy danh sách điểm khởi hành thành công khi có dữ liệu")
    void getAllDepartures_whenLocationsExist_shouldReturnListOfDTOs() {
        System.out.println("Test Case: Valid Input - Lấy danh sách điểm khởi hành thành công.");
        // Arrange
        Location haNoi = new Location();
        haNoi.setId(1L);
        haNoi.setName("Hà Nội");

        Location daNang = new Location();
        daNang.setId(2L);
        daNang.setName("Đà Nẵng");

        List<Location> mockLocations = List.of(haNoi, daNang);

        LocationDTO haNoiDTO = new LocationDTO();
        haNoiDTO.setId(1L);
        haNoiDTO.setName("Hà Nội");

        LocationDTO daNangDTO = new LocationDTO();
        daNangDTO.setId(2L);
        daNangDTO.setName("Đà Nẵng");

        when(locationRepository.findByDeletedFalseOrderByNameAsc()).thenReturn(mockLocations);
        when(locationMapper.toDTO(haNoi)).thenReturn(haNoiDTO);
        when(locationMapper.toDTO(daNang)).thenReturn(daNangDTO);

        // Act
        List<LocationDTO> result = locationService.getAllDepartures();

        // Assert
        assertNotNull(result, "Kết quả trả về không được là null.");
        assertEquals(2, result.size(), "Số lượng địa điểm trả về phải là 2.");
        assertEquals("Hà Nội", result.get(0).getName(), "Tên địa điểm đầu tiên không khớp.");

        // Verify
        verify(locationRepository, times(1)).findByDeletedFalseOrderByNameAsc();
        verify(locationMapper, times(2)).toDTO(any(Location.class));
        System.out.println("Log: " + Constants.Message.GET_LOCATIONS_SUCCESS);
    }

    @Test
    @Order(7)
    @DisplayName("[getAllDepartures] Valid Case: Trả về danh sách rỗng khi không có điểm khởi hành nào")
    void getAllDepartures_whenNoLocationsExist_shouldReturnEmptyList() {
        System.out.println("Test Case: Valid Input - Trả về danh sách rỗng khi không có dữ liệu.");
        // Arrange
        when(locationRepository.findByDeletedFalseOrderByNameAsc()).thenReturn(Collections.emptyList());

        // Act
        List<LocationDTO> result = locationService.getAllDepartures();

        // Assert
        assertNotNull(result, "Kết quả trả về không được là null.");
        assertTrue(result.isEmpty(), "Danh sách trả về phải là rỗng.");

        // Verify
        verify(locationRepository, times(1)).findByDeletedFalseOrderByNameAsc();
        verify(locationMapper, never()).toDTO(any(Location.class));
        System.out.println("Log: " + Constants.Message.SUCCESS + ". " + Constants.Message.NO_SERVICES_AVAILABLE);
    }

    @Test
    @Order(8)
    @DisplayName("[getAllDepartures] Invalid Case: Thất bại khi repository ném ra lỗi")
    void getAllDepartures_whenRepositoryThrowsException_shouldPropagateException() {
        System.out.println("Test Case: Invalid Input - Thất bại khi repository ném ra lỗi.");
        // Arrange
        when(locationRepository.findByDeletedFalseOrderByNameAsc()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationService.getAllDepartures();
        });

        assertEquals("Database connection failed", exception.getMessage());

        // Verify
        verify(locationRepository, times(1)).findByDeletedFalseOrderByNameAsc();
        verify(locationMapper, never()).toDTO(any(Location.class));
        System.out.println("Log: " + Constants.Message.FAILED + ". " + Constants.Message.GET_LOCATIONS_FAIL);
    }
}