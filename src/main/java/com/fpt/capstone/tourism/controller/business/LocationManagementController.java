package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
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
    //postman http://localhost:8080/v1/business/locations?page=0&size=10
    public ResponseEntity<GeneralResponse<PagingDTO<LocationDTO>>> getLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(locationService.getListLocation(page, size));
    }

    @PostMapping("/locations")
    //        "name","description","image"
    public ResponseEntity<GeneralResponse<LocationDTO>> createLocation(@RequestBody LocationRequestDTO requestDTO) {
        return ResponseEntity.ok(locationService.saveLocation(requestDTO));
    }
}