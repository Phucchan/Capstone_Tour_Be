package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourScheduleCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleManagerDTO;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class TourScheduleManagementController {

    @Autowired
    private final TourManagementService tourManagementService;

    @PostMapping("/tours/{tourId}/schedules")
    public ResponseEntity<GeneralResponse<TourScheduleManagerDTO>> createSchedule(
            @PathVariable Long tourId,
            @RequestBody TourScheduleCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTourSchedule(tourId, requestDTO));
    }
}