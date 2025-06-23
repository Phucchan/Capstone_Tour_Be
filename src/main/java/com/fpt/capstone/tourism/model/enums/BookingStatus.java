package com.fpt.capstone.tourism.model.enums;

public enum BookingStatus {
    PENDING,        // Booking is pending confirmation
    CONFIRMED,      // Booking has been confirmed
    CANCELLED,      // Booking has been cancelled
    COMPLETED,      // Booking has been completed
    NO_SHOW,        // Customer did not show up for the booking
    REFUNDED;       // Booking has been refunded
}
