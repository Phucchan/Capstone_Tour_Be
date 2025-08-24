package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDTO;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.enums.TourType;
// Sửa lỗi tại đây: thay đổi đường dẫn import
import com.fpt.capstone.tourism.service.payment.PaymentBillItemRepository;
import com.fpt.capstone.tourism.repository.RefundRepository;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.booking.BookingServiceRepository;
import com.fpt.capstone.tourism.service.payment.PaymentBillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountantServiceImplTest { // Đổi tên class để bao quát toàn bộ service

    @InjectMocks
    private AccountantServiceImpl service;

    @Mock private BookingRepository bookingRepository;
    @Mock private RefundRepository refundRepository;
    @Mock private PaymentBillRepository paymentBillRepository;
    @Mock private PaymentBillItemRepository paymentBillItemRepository;
    @Mock private BookingServiceRepository bookingServiceRepository;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Test
    @DisplayName("getRefundRequests: trả về danh sách & map đúng DTO (search='BK001', page=0, size=10)")
    void getRefundRequests_ReturnsMappedDtos() {
        // Arrange (tham số thật)
        String search = "BK001";
        int page = 0, size = 10;

        Object[] r1 = new Object[]{
                100L,                 // bookingId
                "TC01",               // tourCode
                "Tour Ha Long",       // tourName
                TourType.FIXED,       // tourType
                Timestamp.valueOf(LocalDateTime.of(2025,1,1,9,0)),
                BookingStatus.CANCEL_REQUESTED, // status
                "Nguyen Van A"        // customerName
        };
        Object[] r2 = new Object[]{
                101L, "TC02", "Tour Da Nang",
                TourType.FIXED, Timestamp.valueOf(LocalDateTime.of(2025,2,2,8,0)),
                BookingStatus.REFUNDED, "Le Thi B"
        };
        Page<Object[]> mockPage = new PageImpl<>(List.of(r1, r2), PageRequest.of(page, size), 2);

        when(bookingRepository.findRefundRequests(eq(search), eq(BookingStatus.CANCEL_REQUESTED.name()), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        GeneralResponse<PagingDTO<BookingRefundDTO>> res =
                service.getRefundRequests(search, BookingStatus.CANCEL_REQUESTED, page, size);

        // Assert: status/message
        assertNotNull(res);
        assertEquals(HttpStatus.OK.value(), res.getStatus());
        assertEquals("Success", res.getMessage());

        // Assert: paging info từ resultPage
        PagingDTO<BookingRefundDTO> paging = res.getData();
        assertNotNull(paging);
        assertEquals(page, paging.getPage());
        assertEquals(size, paging.getSize());
        assertEquals(2, paging.getTotal());
        assertEquals(2, paging.getItems().size());

        // Assert: mapping từng item
        BookingRefundDTO d1 = paging.getItems().get(0);
        assertEquals(100L, d1.getBookingId());
        assertEquals("TC01", d1.getTourCode());
        assertEquals("Tour Ha Long", d1.getTourName());
        assertEquals("FIXED", d1.getTourType());
        assertEquals("CANCEL_REQUESTED", d1.getStatus());
        assertEquals("Nguyen Van A", d1.getCustomerName());

        // Verify: repository được gọi với search & pageable đúng (page/size/sort)
        verify(bookingRepository).findRefundRequests(eq(search), eq(BookingStatus.CANCEL_REQUESTED.name()), pageableCaptor.capture());
        Pageable used = pageableCaptor.getValue();
        assertEquals(page, used.getPageNumber());
        assertEquals(size, used.getPageSize());
        // sort by "start_date" desc
        Sort.Order order = used.getSort().getOrderFor("start_date");
        assertNotNull(order);
        assertEquals(Sort.Direction.DESC, order.getDirection());

        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    @DisplayName("getRefundRequests: trả về rỗng khi không có bản ghi (search='', page=1, size=5)")
    void getRefundRequests_Empty() {
        // Arrange
        String search = "";
        int page = 1, size = 5;
        Page<Object[]> mockPage = new PageImpl<>(List.of(), PageRequest.of(page, size), 0);

        when(bookingRepository.findRefundRequests(eq(search), isNull(), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act
        GeneralResponse<PagingDTO<BookingRefundDTO>> res =
                service.getRefundRequests(search, null, page, size);

        // Assert
        assertEquals(HttpStatus.OK.value(), res.getStatus());
        assertEquals("Success", res.getMessage());
        assertNotNull(res.getData());
        assertEquals(0, res.getData().getItems().size());
        assertEquals(0, res.getData().getTotal());
        assertEquals(page, res.getData().getPage());
        assertEquals(size, res.getData().getSize());

        verify(bookingRepository).findRefundRequests(eq(search), isNull(), any(Pageable.class));
        verifyNoMoreInteractions(bookingRepository);
    }

    // Bạn có thể thêm các test case khác cho các phương thức còn lại ở đây
}