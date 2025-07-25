package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Hotel {
    private int id;
    private String name;
    private String websiteUrl;
    private String logoUrl;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String roomDetails;
    private double total;
    private double estimatedCost;
}
