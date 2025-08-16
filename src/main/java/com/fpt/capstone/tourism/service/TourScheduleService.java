package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourScheduleCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleOptionsDTO;
import java.util.List;

public interface TourScheduleService {
    GeneralResponse<List<TourScheduleManagerDTO>> createTourSchedule(Long tourId, TourScheduleCreateRequestDTO requestDTO);

    GeneralResponse<TourScheduleOptionsDTO> getScheduleOptions(Long tourId);

    GeneralResponse<List<TourScheduleManagerDTO>> getTourSchedules(Long tourId);

    GeneralResponse<String> deleteTourSchedule(Long tourId, Long scheduleId);
}