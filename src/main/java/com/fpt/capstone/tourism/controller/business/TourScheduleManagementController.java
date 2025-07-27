package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourScheduleCreateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleManagerDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourScheduleOptionsDTO;
import com.fpt.capstone.tourism.service.TourManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class TourScheduleManagementController {

    @Autowired
    private final TourManagementService tourManagementService;

    @PostMapping("/tours/{tourId}/schedules")
    //postman http://localhost:8080/v1/business/tours/1/schedules
    public ResponseEntity<GeneralResponse<TourScheduleManagerDTO>> createSchedule(
            @PathVariable Long tourId,
            @RequestBody TourScheduleCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(tourManagementService.createTourSchedule(tourId, requestDTO));
    }
    @GetMapping("/tours/{tourId}/schedule-options")
    //postman http://localhost:8080/v1/business/tours/1/schedule-options
    public ResponseEntity<GeneralResponse<TourScheduleOptionsDTO>> getScheduleOptions(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourManagementService.getScheduleOptions(tourId));
    }
    @GetMapping("/tours/{tourId}/schedules")
    // Example: http://localhost:8080/v1/business/tours/1/schedules
    public ResponseEntity<GeneralResponse<List<TourScheduleManagerDTO>>> getSchedules(
            @PathVariable Long tourId) {
        return ResponseEntity.ok(tourManagementService.getTourSchedules(tourId));
    }
}