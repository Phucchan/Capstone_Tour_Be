package com.fpt.capstone.tourism.dto.response.homepage;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourSummaryDTO {
    private Long id;
    private Long scheduleId;
    private String name;
    private String thumbnailUrl;
    private Double averageRating;
    private int durationDays;
    private String region;
    private String locationName;
    private Double startingPrice;
    private String code;
    private String tourTransport;
    private List<LocalDateTime> departureDates;
}
