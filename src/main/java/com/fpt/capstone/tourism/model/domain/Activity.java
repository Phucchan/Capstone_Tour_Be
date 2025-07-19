package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Activity {
    private int id;
    private String title;
    private String content;
    private String category;
    private String duration;
    private String imageUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double estimatedCost;
}
