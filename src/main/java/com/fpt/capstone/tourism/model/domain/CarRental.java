package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarRental {
    private String id;
    private String providerName;
    private String carType;         // SUV, Sedan, 7-seater, etc.
    private String brand;           // Toyota, Ford, Kia...
    private String licensePlate;
    private String pickupLocation;
    private String dropLocation;
    private int seatCapacity;
}