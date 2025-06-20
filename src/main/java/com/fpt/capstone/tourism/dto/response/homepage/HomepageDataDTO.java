package com.fpt.capstone.tourism.dto.response.homepage;
import lombok.*;

import java.util.List;

@Data
@Builder
public class HomepageDataDTO {
    /**
     * A list of tour themes to be displayed.
     */
    private List<TourThemeDTO> themes;

    /**
     * A list of top-rated or featured tours.
     */
    private List<TourSummaryDTO> highlyRatedTours;

    /**
     * A list of recent blog posts.
     */
    private List<BlogSummaryDTO> recentBlogs;
}
