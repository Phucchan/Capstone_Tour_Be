package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.dto.request.seller.BookingCustomerUpdateDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.seller.SellerBookingUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingDetailDTO;
import com.fpt.capstone.tourism.dto.response.seller.SellerBookingSummaryDTO;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.tour.Booking;

import java.util.List;

public interface SellerBookingService {
    GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> getAvailableBookings(int page, int size);

    GeneralResponse<PagingDTO<SellerBookingSummaryDTO>> getEditedTours(String sellerUsername, int page, int size);

    GeneralResponse<SellerBookingDetailDTO> getBookingDetail(Long bookingId);

    GeneralResponse<SellerBookingDetailDTO> updateBookingSchedule(Long bookingId, Long scheduleId);

    GeneralResponse<SellerBookingDetailDTO> claimBooking(Long bookingId, String sellerUsername);

    GeneralResponse<SellerBookingDetailDTO> updateBookedPerson(Long bookingId, SellerBookingUpdateRequestDTO requestDTO);

    GeneralResponse<SellerBookingDetailDTO> updateBookingStatus(Long bookingId, BookingStatus status);

    GeneralResponse<SellerBookingDetailDTO> updateCustomer(Long customerId, BookingRequestCustomerDTO requestDTO);

    GeneralResponse<SellerBookingDetailDTO> deleteCustomer(Long customerId);
}