package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.LocationRequestDTO;
import com.fpt.capstone.tourism.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class LocationManagementController {

    @Autowired
    private final LocationService locationService;

    @GetMapping("/locations")
    public ResponseEntity<GeneralResponse<List<LocationDTO>>> getLocations() {
        return ResponseEntity.ok(locationService.getListLocation());
    }
    @GetMapping("/locations/search")
    //        "name"
    public ResponseEntity<GeneralResponse<List<LocationDTO>>> searchLocations(@RequestParam String name) {
        return ResponseEntity.ok(locationService.searchLocations(name));
    }
    @PostMapping("/locations")
    //        "name","description","image"
    public ResponseEntity<GeneralResponse<LocationDTO>> createLocation(@RequestBody LocationRequestDTO requestDTO) {
        return ResponseEntity.ok(locationService.saveLocation(requestDTO));
    }
}