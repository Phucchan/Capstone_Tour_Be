package com.fpt.capstone.tourism.dto.request.tourManager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourUpdateManagerRequestDTO {
    private String name;
    private String thumbnailUrl;
    private String description;
    private String tourStatus; // <-- THÊM TRƯỜNG NÀY VÀO
    private Long departLocationId;
    private List<Long> tourThemeIds;
    private List<Long> destinationLocationIds;
    // Không cần durationDays vì nó sẽ được tính toán lại tự động
}