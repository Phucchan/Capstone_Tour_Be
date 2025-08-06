package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDTO;
import com.fpt.capstone.tourism.service.AccountantService;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountantServiceImpl implements AccountantService {

    private final BookingRepository bookingRepository;

    @Override
    public GeneralResponse<PagingDTO<BookingRefundDTO>> getRefundRequests(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("start_date").descending());
        Page<Object[]> resultPage = bookingRepository.findRefundRequests(search, pageable);

        List<BookingRefundDTO> items = resultPage.getContent().stream()
                .map(this::mapToDto)
                .toList();

        PagingDTO<BookingRefundDTO> pagingDTO = PagingDTO.<BookingRefundDTO>builder()
                .page(resultPage.getNumber())
                .size(resultPage.getSize())
                .total(resultPage.getTotalElements())
                .items(items)
                .build();

        return new GeneralResponse<>(HttpStatus.OK.value(), "Success", pagingDTO);
    }

    private BookingRefundDTO mapToDto(Object[] row) {
        String tourCode = (String) row[0];
        String tourName = (String) row[1];
        String tourType = row[2] != null ? row[2].toString() : null;
        LocalDateTime startDate = row[3] != null ? ((Timestamp) row[3]).toLocalDateTime() : null;
        String status = row[4] != null ? row[4].toString() : null;
        String customerName = (String) row[5];

        return BookingRefundDTO.builder()
                .tourCode(tourCode)
                .tourName(tourName)
                .tourType(tourType)
                .startDate(startDate)
                .status(status)
                .customerName(customerName)
                .build();
    }
}