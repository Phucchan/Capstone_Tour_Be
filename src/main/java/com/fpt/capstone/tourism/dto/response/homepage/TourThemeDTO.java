package com.fpt.capstone.tourism.dto.response.homepage;
import lombok.*;

@Data
@Builder
public class TourThemeDTO {
    /**
     * The name of the theme (e.g., "Du lịch mạo hiểm").
     */
    private String name;

    /**
     * The URL for the representative image of the theme.
     */
    private String imageUrl;
}
