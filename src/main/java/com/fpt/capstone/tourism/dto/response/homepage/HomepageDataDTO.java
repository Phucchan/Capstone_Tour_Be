package com.fpt.capstone.tourism.dto.response.homepage;
import lombok.*;
import com.fpt.capstone.tourism.dto.common.location.LocationWithoutGeoPositionDTO;
import java.util.List;

@Data
@Builder
public class HomepageDataDTO {
    /**
     * A list of featured locations.
     */
    private List<LocationWithoutGeoPositionDTO> locations;
    /**
     * A list of top-rated or featured tours.
     */
    private List<TourSummaryDTO> highlyRatedTours;
    /**
     * A list of tours currently on sale or promotion.
     */
    private List<TourSummaryDTO> saleTours;
    /**
     * A list of recent blog posts.
     */
    private List<BlogSummaryDTO> recentBlogs;
}
