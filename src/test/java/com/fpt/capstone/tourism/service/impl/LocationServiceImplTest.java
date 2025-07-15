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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class LocationServiceImplTest {

    @InjectMocks
    private LocationServiceImpl locationService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private LocationMapper locationMapper;

    @Captor
    private ArgumentCaptor<Location> locationArgumentCaptor;

    private Location location;
    private LocationDTO locationDTO;
    private LocationRequestDTO locationRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        location = new Location();
        location.setId(1L);
        location.setName("Hanoi");
        location.setDeleted(false);

        locationDTO = new LocationDTO();
        locationDTO.setId(1L);
        locationDTO.setName("Hanoi");

        locationRequestDTO = new LocationRequestDTO();
        locationRequestDTO.setName("Hanoi");
        locationRequestDTO.setDescription("Capital of Vietnam");
    }

    // Tests for getAllDepartures
    @Test
    void getAllDepartures_shouldReturnListOfDepartures_whenDeparturesExist() {
        // Arrange
        List<Location> locations = List.of(location);
        when(tourRepository.findDistinctDepartLocations()).thenReturn(locations);
        when(locationMapper.toDTO(location)).thenReturn(locationDTO);

        // Act
        List<LocationDTO> result = locationService.getAllDepartures();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(locationDTO.getName(), result.get(0).getName());
        verify(tourRepository, times(1)).findDistinctDepartLocations();
        verify(locationMapper, times(1)).toDTO(location);
    }

    @Test
    void getAllDepartures_shouldReturnEmptyList_whenNoDeparturesExist() {
        // Arrange
        when(tourRepository.findDistinctDepartLocations()).thenReturn(Collections.emptyList());

        // Act
        List<LocationDTO> result = locationService.getAllDepartures();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tourRepository, times(1)).findDistinctDepartLocations();
        verify(locationMapper, never()).toDTO(any(Location.class));
    }

    // Tests for getAllDestinations
    @Test
    void getAllDestinations_shouldReturnListOfDestinations_whenDestinationsExist() {
        // Arrange
        List<Location> locations = List.of(location);
        when(tourRepository.findDistinctDestinations()).thenReturn(locations);
        when(locationMapper.toDTO(location)).thenReturn(locationDTO);

        // Act
        List<LocationDTO> result = locationService.getAllDestinations();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(locationDTO.getName(), result.get(0).getName());
        verify(tourRepository, times(1)).findDistinctDestinations();
        verify(locationMapper, times(1)).toDTO(location);
    }

    @Test
    void getAllDestinations_shouldReturnEmptyList_whenNoDestinationsExist() {
        // Arrange
        when(tourRepository.findDistinctDestinations()).thenReturn(Collections.emptyList());

        // Act
        List<LocationDTO> result = locationService.getAllDestinations();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(tourRepository, times(1)).findDistinctDestinations();
        verify(locationMapper, never()).toDTO(any(Location.class));
    }

    // Tests for saveLocation

    @Test
    void saveLocation_shouldThrowBusinessException_whenNameExists() {
        // Arrange
        when(locationRepository.findByName(locationRequestDTO.getName())).thenReturn(new Location());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> locationService.saveLocation(locationRequestDTO));

        assertEquals(Constants.Message.EXISTED_LOCATION, exception.getMessage());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void saveLocation_shouldThrowBusinessException_whenRepositoryFails() {
        // Arrange
        when(locationRepository.findByName(locationRequestDTO.getName())).thenReturn(null);
        when(locationMapper.toEntity(locationRequestDTO)).thenReturn(location);
        when(locationRepository.save(any(Location.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> locationService.saveLocation(locationRequestDTO));

        assertEquals(Constants.Message.CREATE_LOCATION_FAIL, exception.getMessage());
    }

    // Tests for getLocationById
    @Test
    void getLocationById_shouldReturnLocation_whenFound() {
        // Arrange
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationMapper.toDTO(location)).thenReturn(locationDTO);

        // Act
        GeneralResponse<LocationDTO> response = locationService.getLocationById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(Constants.Message.GENERAL_SUCCESS_MESSAGE, response.getMessage());
        assertNotNull(response.getData());
        assertEquals(locationDTO.getId(), response.getData().getId());
        verify(locationRepository, times(1)).findById(1L);
    }
    }

