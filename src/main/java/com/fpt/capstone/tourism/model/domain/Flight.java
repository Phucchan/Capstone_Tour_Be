package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Flight {
    private String id;
    private String flightNumber;
    private String airline;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private String seatClass; // Economy, Business, First, etc.
}