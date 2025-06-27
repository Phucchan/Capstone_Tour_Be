package com.fpt.capstone.tourism.dto.response.homepage;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourSummaryDTO {
    /**
     * The unique identifier for the tour.
     */
    private Long id;

    /**
     * The name of the tour.
     */
    private String name;

    /**
     * The URL for the tour's thumbnail image.
     */
    private String thumbnailUrl;

    /**
     * The average rating of the tour. Can be null if there are no ratings.
     */
    private Double averageRating;

    /**
     * The duration of the tour in days.
     */
    private int durationDays;

    /**
     * The region of the tour (e.g., "NORTH", "CENTRAL", "SOUTH").
     */
    private String region;

    /**
     * The name of the primary or departure location.
     */
    private String locationName;

    /**
     * The starting price for the tour, typically for the smallest group size.
     */
    private Double startingPrice;

    private String code;


    private String tourTransport;


    private List<LocalDateTime> departureDates;
}
