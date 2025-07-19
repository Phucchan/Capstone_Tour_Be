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
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import org.junit.jupiter.api.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    private Location location;
    private LocationDTO locationDTO;
    private LocationRequestDTO locationRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        location = new Location();
        location.setId(1L);
        location.setName("Hà Nội");
        locationDTO = new LocationDTO();
        locationDTO.setId(1L);
        locationDTO.setName("Hà Nội");
        locationRequestDTO = new LocationRequestDTO();
        locationRequestDTO.setName("Hà Nội");
        locationRequestDTO.setDescription("Thủ đô Việt Nam");
        locationRequestDTO.setImage("https://example.com/image.jpg");
    }

    @Test
    @Order(1)
    void saveLocation_Success() {
        when(locationRepository.findByName("Hà Nội")).thenReturn(null);
        when(locationMapper.toEntity(locationRequestDTO)).thenReturn(location);
        when(locationMapper.toDTO(location)).thenReturn(locationDTO);

        GeneralResponse<LocationDTO> response = locationService.saveLocation(locationRequestDTO);

        assertEquals(200, response.getStatus());
        assertEquals("Tạo địa điểm thành công", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("Hà Nội", response.getData().getName());
    }

    @Test
    @Order(2)
    void getLocationById_Success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationMapper.toDTO(location)).thenReturn(locationDTO);

        GeneralResponse<LocationDTO> response = locationService.getLocationById(1L);

        assertEquals(200, response.getStatus());
        assertEquals(Constants.Message.GENERAL_SUCCESS_MESSAGE, response.getMessage());
        assertNotNull(response.getData());
        assertEquals("Hà Nội", response.getData().getName());
    }

    @Test
    @Order(3)
    void deleteLocation_Success() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationMapper.toDTO(location)).thenReturn(locationDTO);

        GeneralResponse<LocationDTO> response = locationService.deleteLocation(1L, true);

        assertEquals(200, response.getStatus());
        assertEquals(Constants.Message.GENERAL_SUCCESS_MESSAGE, response.getMessage());
        assertFalse(response.getData().isDeleted());
    }

    // Abnormal cases
    @Test
    @Order(4)
    void saveLocation_ShouldThrowException_WhenNameIsNumeric() {
        locationRequestDTO.setName("123");
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.saveLocation(locationRequestDTO));
        assertEquals(Constants.Message.CREATE_LOCATION_FAIL, exception.getResponseMessage());
    }

    @Test
    @Order(5)
    void saveLocation_ShouldThrowException_WhenNameIsDuplicate() {
        when(locationRepository.findByName("Hà Nội")).thenReturn(location);
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.saveLocation(locationRequestDTO));
        assertEquals(Constants.Message.EXISTED_LOCATION, exception.getResponseMessage());
    }

    @Test
    @Order(6)
    void saveLocation_ShouldThrowException_WhenMapperReturnsNull() {
        when(locationRepository.findByName(anyString())).thenReturn(null);
        when(locationMapper.toEntity(locationRequestDTO)).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.saveLocation(locationRequestDTO));
        assertEquals(Constants.Message.CREATE_LOCATION_FAIL, exception.getResponseMessage());
    }

    @Test
    @Order(7)
    void getLocationById_ShouldThrowException_WhenNotFound() {
        when(locationRepository.findById(2L)).thenReturn(Optional.empty());
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.getLocationById(2L));
        assertEquals(Constants.Message.GENERAL_FAIL_MESSAGE, exception.getResponseMessage());
    }

    @Test
    @Order(8)
    void deleteLocation_ShouldThrowException_WhenNotFound() {
        when(locationRepository.findById(2L)).thenReturn(Optional.empty());
        when(locationMapper.toEntity(any(LocationRequestDTO.class))).thenReturn(null);
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.deleteLocation(2L, true));
        assertEquals(Constants.Message.GENERAL_FAIL_MESSAGE, exception.getResponseMessage());
    }

    // Boundary cases
    @Test
    @Order(9)
    void saveLocation_ShouldThrowException_WhenNameIsEmpty() {
        locationRequestDTO.setName("");
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.saveLocation(locationRequestDTO));
        assertEquals(Constants.Message.EMPTY_LOCATION_NAME, exception.getResponseMessage());
    }

    @Test
    @Order(10)
    void saveLocation_ShouldThrowException_WhenDescriptionIsEmpty() {
        locationRequestDTO.setDescription("");
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.saveLocation(locationRequestDTO));
        assertEquals(Constants.Message.EMPTY_LOCATION_DESCRIPTION, exception.getResponseMessage());
    }

    @Test
    @Order(11)
    void saveLocation_ShouldThrowException_WhenImageIsEmpty() {
        locationRequestDTO.setImage("");
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.saveLocation(locationRequestDTO));
        assertEquals(Constants.Message.EMPTY_LOCATION_IMAGE, exception.getResponseMessage());
    }

    @Test
    @Order(12)
    void getListLocation_Success_WithKeyword() {
        Page<Location> page = new PageImpl<>(List.of(location));
        when(locationRepository.findByNameContainingIgnoreCase(anyString(), any())).thenReturn(page);
        when(locationMapper.toDTO(any())).thenReturn(locationDTO);

        GeneralResponse<PagingDTO<LocationDTO>> response = locationService.getListLocation(0, 10, "Hà Nội");
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getItems().size());
    }

    @Test
    @Order(13)
    void getListLocation_Success_WithoutKeyword() {
        Page<Location> page = new PageImpl<>(List.of(location));
        when(locationRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(locationMapper.toDTO(any())).thenReturn(locationDTO);

        GeneralResponse<PagingDTO<LocationDTO>> response = locationService.getListLocation(0, 10, null);
        assertEquals(200, response.getStatus());
        assertNotNull(response.getData());
        assertEquals(1, response.getData().getItems().size());
    }

    @Test
    @Order(14)
    void getListLocation_ShouldThrowException_OnRepositoryError() {
        when(locationRepository.findAll(any(Pageable.class))).thenThrow(new RuntimeException("DB error"));
        BusinessException exception = assertThrows(BusinessException.class, () -> locationService.getListLocation(0, 10, null));
        assertEquals(Constants.Message.GET_LOCATIONS_FAIL, exception.getResponseMessage());
    }
}
