package com.fpt.capstone.tourism.dto.response.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckInDTO {
    private Long id;
    private String imageUrl;
    private LocalDateTime createdAt;
    private Integer pointsEarned;
}