package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.PartnerSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.PartnerMapper;
import com.fpt.capstone.tourism.model.partner.Partner;
import com.fpt.capstone.tourism.repository.partner.PartnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

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
}