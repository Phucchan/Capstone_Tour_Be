package com.fpt.capstone.tourism.controller;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/departures")
    public ResponseEntity<GeneralResponse<List<LocationDTO>>> getAllDepartures() {
        List<LocationDTO> result = locationService.getAllDepartures();
        return ResponseEntity.ok(GeneralResponse.of(result));
    }

    @GetMapping("/destinations")
    public ResponseEntity<GeneralResponse<List<LocationDTO>>> getAllDestinations() {
        List<LocationDTO> result = locationService.getAllDestinations();
        return ResponseEntity.ok(GeneralResponse.of(result));
    }
}
