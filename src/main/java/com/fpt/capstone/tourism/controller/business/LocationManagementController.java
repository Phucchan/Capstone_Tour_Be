package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.ChangeDeleteStatusDTO;
import com.fpt.capstone.tourism.dto.request.LocationRequestDTO;
import com.fpt.capstone.tourism.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(locationService.getListLocation(page, size, keyword));
    }

    @PostMapping(value = "/locations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<LocationDTO>> createLocation(@RequestParam("file") MultipartFile file,
                                                                       @ModelAttribute LocationRequestDTO requestDTO) {
        return ResponseEntity.ok(locationService.saveLocation(requestDTO, file));
    }
    @GetMapping("/locations/{id}")
    //postman http://localhost:8080/v1/business/locations/1
    public ResponseEntity<GeneralResponse<LocationDTO>>locationDetail(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PutMapping(value = "/locations/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //postman http://localhost:8080/v1/business/locations/1
    public ResponseEntity<GeneralResponse<LocationDTO>> updateLocation(@PathVariable Long id,
                                                                       @RequestParam(value = "file", required = false) MultipartFile file,
                                                                       @ModelAttribute LocationRequestDTO requestDTO) {
        return ResponseEntity.ok(locationService.updateLocation(id, requestDTO, file));
    }
    @PatchMapping("/locations/{id}/status")
    public ResponseEntity<GeneralResponse<LocationDTO>> changeLocationStatus(@PathVariable Long id,
                                                                             @RequestBody ChangeDeleteStatusDTO changeStatusDTO) {
        return ResponseEntity.ok(locationService.deleteLocation(id, changeStatusDTO.getDeleted()));
    }
}