package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.seller.BookingCustomerUpdateDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingDetailDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
import com.fpt.capstone.tourism.model.tour.Booking;

import java.util.List;

public interface SellerBookingService {
    GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> getAvailableBookings(int page, int size);

    GeneralResponse<Booking> updateBooking(Long bookingId, String sellerUsername,
                                           List<BookingCustomerUpdateDTO> customers);

    GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> getEditedTours(String sellerUsername, int page, int size);

    GeneralResponse<SellerBookingDetailDTO> getBookingDetail(Long bookingId);
}