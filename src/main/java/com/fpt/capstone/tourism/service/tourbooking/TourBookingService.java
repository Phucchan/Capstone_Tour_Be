package com.fpt.capstone.tourism.service.tourbooking;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.booking.BookingBasicRequestDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestCustomerDTO;
import com.fpt.capstone.tourism.dto.request.booking.BookingRequestDTO;
import com.fpt.capstone.tourism.dto.response.booking.BookingConfirmResponse;
import com.fpt.capstone.tourism.model.enums.PaymentMethod;
import com.fpt.capstone.tourism.model.tour.Booking;

public interface TourBookingService {
    String createBooking(BookingRequestDTO bookingRequestDTO);
    String createBasicBooking(BookingBasicRequestDTO requestDTO);
    void addCustomers(String bookingCode, java.util.List<BookingRequestCustomerDTO> customers);
    void saveTourBookingService(Booking booking, int totalCustomers);
    void createReceiptBookingBill(Booking tourBooking, Double total, String fullName, PaymentMethod paymentMethod);

    void confirmPayment(int paymentStatus, String orderInfo);

    BookingConfirmResponse getTourBookingDetails(String bookingCode);
}
