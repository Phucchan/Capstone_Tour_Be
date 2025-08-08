package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.accountatn.RefundBillRequestDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingListDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDetailDTO;

public interface AccountantService {
    GeneralResponse<PagingDTO<BookingRefundDTO>> getRefundRequests(String search, int page, int size);
    GeneralResponse<BookingRefundDetailDTO> getRefundRequestDetail(Long bookingId);
    GeneralResponse<BookingRefundDetailDTO> cancelRefundRequest(Long bookingId);
    GeneralResponse<BookingRefundDetailDTO> createRefundBill(Long bookingId, RefundBillRequestDTO request);
    GeneralResponse<PagingDTO<BookingListDTO>> getBookings(String search, int page, int size);
}
