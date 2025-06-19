package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;

public interface HomepageService {
    /**
     * Gathers all necessary data for the homepage display.
     * @return HomepageDataDTO containing lists of themes, top-rated tours, and recent blogs.
     */
    HomepageDataDTO getHomepageData();
}
