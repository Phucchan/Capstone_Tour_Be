package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.accountant.BookingRefundDTO;

public interface AccountantService {
    GeneralResponse<PagingDTO<BookingRefundDTO>> getRefundRequests(String search, int page, int size);
}
