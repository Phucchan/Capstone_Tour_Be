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
import com.fpt.capstone.tourism.model.enums.VoucherStatus;
import com.fpt.capstone.tourism.model.voucher.Voucher;
import com.fpt.capstone.tourism.repository.voucher.VoucherRepository;
import com.fpt.capstone.tourism.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Captor
    private ArgumentCaptor<Voucher> voucherCaptor;

    private VoucherRequestDTO requestDTO;
    private User creator;
    private Voucher savedVoucher;
    private VoucherDTO savedVoucherDTO;

    @BeforeEach
    void setUp() {
        creator = User.builder().id(1L).fullName("Admin").build();

        requestDTO = VoucherRequestDTO.builder()
                .code("SUMMER2024")
                .description("Summer Sale")
                .discountAmount(50000.0)
                .pointsRequired(100)
                .minOrderValue(500000.0)
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusMonths(1))
                .maxUsage(1000)
                .createdBy(creator.getId())
                .build();

        savedVoucher = Voucher.builder().id(1L).code("SUMMER2024").build();
        savedVoucherDTO = VoucherDTO.builder().id(1L).code("SUMMER2024").build();
    }

    // region createVoucher Tests

    @Test
    void createVoucher_Normal_ValidInput_ShouldReturnSuccess() {
        // Arrange
        when(voucherRepository.findByCode(requestDTO.getCode())).thenReturn(Optional.empty());
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(voucherRepository.save(any(Voucher.class))).thenReturn(savedVoucher);
        when(voucherMapper.toDTO(savedVoucher)).thenReturn(savedVoucherDTO);

        // Act
        GeneralResponse<VoucherDTO> response = voucherService.createVoucher(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.VOUCHER_CREATE_SUCCESS, response.getMessage());
        assertEquals(savedVoucherDTO, response.getData());

        verify(voucherRepository).save(voucherCaptor.capture());
        Voucher capturedVoucher = voucherCaptor.getValue();
        assertEquals(requestDTO.getCode(), capturedVoucher.getCode());
        assertEquals(VoucherStatus.ACTIVE, capturedVoucher.getVoucherStatus());
        assertFalse(capturedVoucher.getDeleted());
    }

    @Test
    void createVoucher_Abnormal_CodeAlreadyExists_ShouldThrowBusinessException() {
        // Arrange
        when(voucherRepository.findByCode(requestDTO.getCode())).thenReturn(Optional.of(new Voucher()));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> voucherService.createVoucher(requestDTO));

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getHttpCode());
        assertEquals(Constants.Message.VOUCHER_CODE_EXISTS, exception.getMessage());
        verify(voucherRepository, never()).save(any());
    }

    @Test
    void createVoucher_Abnormal_CreatorNotFound_ShouldThrowBusinessException() {
        // Arrange
        when(voucherRepository.findByCode(requestDTO.getCode())).thenReturn(Optional.empty());
        when(userService.findById(creator.getId())).thenThrow(BusinessException.of(HttpStatus.NOT_FOUND, Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> voucherService.createVoucher(requestDTO));

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getHttpCode());
        assertEquals(Constants.UserExceptionInformation.USER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    void createVoucher_Abnormal_NullInputDTO_ShouldThrowBusinessException() {
        // Act & Assert
        // The service's catch-all will wrap the NullPointerException
        BusinessException exception = assertThrows(BusinessException.class,
                () -> voucherService.createVoucher(null));
        assertEquals(Constants.Message.VOUCHER_CREATE_FAIL, exception.getMessage());
    }

    @Test
    void createVoucher_Abnormal_NullCodeInDTO_ShouldThrowBusinessException() {
        // Arrange
        requestDTO.setCode(null);

        // Act & Assert
        // The service's catch-all will wrap the NullPointerException from requestDTO.getCode()
        BusinessException exception = assertThrows(BusinessException.class,
                () -> voucherService.createVoucher(requestDTO));
        assertEquals(Constants.Message.VOUCHER_CREATE_FAIL, exception.getMessage());
    }

    @Test
    void createVoucher_Boundary_ZeroValues_ShouldSucceed() {
        // Arrange
        requestDTO.setCode("FREEPROMO");
        requestDTO.setPointsRequired(0);
        requestDTO.setMinOrderValue(0.0);
        requestDTO.setDiscountAmount(0.0);

        Voucher zeroValueVoucher = Voucher.builder().id(2L).code("FREEPROMO").pointsRequired(0).minOrderValue(0.0).build();
        VoucherDTO zeroValueVoucherDTO = VoucherDTO.builder().id(2L).code("FREEPROMO").build();

        when(voucherRepository.findByCode("FREEPROMO")).thenReturn(Optional.empty());
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(voucherRepository.save(any(Voucher.class))).thenReturn(zeroValueVoucher);
        when(voucherMapper.toDTO(zeroValueVoucher)).thenReturn(zeroValueVoucherDTO);

        // Act
        GeneralResponse<VoucherDTO> response = voucherService.createVoucher(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.VOUCHER_CREATE_SUCCESS, response.getMessage());

        verify(voucherRepository).save(voucherCaptor.capture());
        Voucher capturedVoucher = voucherCaptor.getValue();
        assertEquals(0, capturedVoucher.getPointsRequired());
        assertEquals(0.0, capturedVoucher.getMinOrderValue());
        assertEquals(0.0, capturedVoucher.getDiscountAmount());
    }

    // endregion

    // region getVouchers Tests

    @Test
    void getVouchers_Normal_WithKeyword_ShouldReturnFilteredVouchers() {
        // Arrange
        String keyword = "SUMMER";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Voucher> voucherPage = new PageImpl<>(List.of(savedVoucher), pageable, 1);

        when(voucherRepository
                .findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(eq(keyword), any(Pageable.class)))
                .thenReturn(voucherPage);
        when(voucherMapper.toSummaryDTO(any(Voucher.class))).thenReturn(new VoucherSummaryDTO());

        // Act
        GeneralResponse<PagingDTO<VoucherSummaryDTO>> response = voucherService.getVouchers(keyword, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.VOUCHER_LIST_SUCCESS, response.getMessage());
        assertEquals(1, response.getData().getItems().size());
        verify(voucherRepository).findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(eq(keyword), any(Pageable.class));
        verify(voucherRepository, never()).findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getVouchers_Normal_WithoutKeyword_ShouldReturnAllVouchers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Voucher> voucherPage = new PageImpl<>(List.of(savedVoucher), pageable, 1);

        when(voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(voucherPage);
        when(voucherMapper.toSummaryDTO(any(Voucher.class))).thenReturn(new VoucherSummaryDTO());

        // Act
        GeneralResponse<PagingDTO<VoucherSummaryDTO>> response = voucherService.getVouchers(null, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.VOUCHER_LIST_SUCCESS, response.getMessage());
        assertEquals(1, response.getData().getItems().size());
        verify(voucherRepository, never()).findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(anyString(), any(Pageable.class));
        verify(voucherRepository).findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getVouchers_Boundary_BlankKeyword_ShouldReturnAllVouchers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Voucher> voucherPage = new PageImpl<>(List.of(savedVoucher), pageable, 1);

        when(voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(voucherPage);
        when(voucherMapper.toSummaryDTO(any(Voucher.class))).thenReturn(new VoucherSummaryDTO());

        // Act
        GeneralResponse<PagingDTO<VoucherSummaryDTO>> response = voucherService.getVouchers("   ", 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(Constants.Message.VOUCHER_LIST_SUCCESS, response.getMessage());
        verify(voucherRepository).findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class));
    }

    @Test
    void getVouchers_Boundary_EmptyResult_ShouldReturnEmptyPagingDTO() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Voucher> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        GeneralResponse<PagingDTO<VoucherSummaryDTO>> response = voucherService.getVouchers(null, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Constants.Message.VOUCHER_LIST_SUCCESS, response.getMessage());
        assertTrue(response.getData().getItems().isEmpty());
        assertEquals(0, response.getData().getTotal());
    }

    @Test
    void getVouchers_Abnormal_RepositoryFails_ShouldThrowBusinessException() {
        // Arrange
        when(voucherRepository.findByDeletedFalseOrderByCreatedAtDesc(any(Pageable.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> voucherService.getVouchers(null, 0, 10));

        assertEquals(Constants.Message.VOUCHER_LIST_FAIL, exception.getMessage());
    }

    // endregion
}