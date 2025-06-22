package com.fpt.capstone.tourism.dto.response.homepage;
import lombok.*;
import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import java.util.List;

@Data
@Builder
public class HomepageDataDTO {
    /**
     * A list of featured locations.
     */
    private List<LocationDTO> locations;
    /**
     * A list of top-rated or featured tours.
     */
    private List<TourSummaryDTO> highlyRatedTours;

    /**
     * A list of recent blog posts.
     */
    private List<BlogSummaryDTO> recentBlogs;
}
