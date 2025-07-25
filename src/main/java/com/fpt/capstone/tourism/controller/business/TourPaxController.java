package com.fpt.capstone.tourism.controller.business;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.tourManager.TourMarkupUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxCreateRequestDTO;
import com.fpt.capstone.tourism.dto.request.tourManager.TourPaxUpdateRequestDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourMarkupResponseDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourPaxFullDTO;
import com.fpt.capstone.tourism.dto.response.tourManager.TourResponseDTO;
import com.fpt.capstone.tourism.service.TourPaxService;
import com.fpt.capstone.tourism.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/business")
public class TourPaxController {
    private final TourPaxService tourPaxService;
    private final TourService tourService;

    @GetMapping
    //postman http://localhost:8080/head-of-business/tour/1/tour-pax
    public ResponseEntity<GeneralResponse<List<TourPaxFullDTO>>> getTourPaxConfigurations(
            @PathVariable Long tourId) {

        return ResponseEntity.ok(tourPaxService.getTourPaxConfigurations(tourId));
    }

    @GetMapping("/detail/{paxId}")
    public ResponseEntity<GeneralResponse<TourPaxFullDTO>> getTourPaxConfiguration(
            @PathVariable Long tourId,
            @PathVariable Long paxId) {
        return ResponseEntity.ok(tourPaxService.getTourPaxConfiguration(tourId, paxId));
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse<TourPaxFullDTO>> createTourPaxConfiguration(
            @PathVariable Long tourId,
            @RequestBody TourPaxCreateRequestDTO request) {
        return ResponseEntity.ok(tourPaxService.createTourPaxConfiguration(tourId, request));
    }

    @PutMapping("/update/{paxId}")
    public ResponseEntity<GeneralResponse<TourPaxFullDTO>> updateTourPaxConfiguration(
            @PathVariable Long tourId,
            @PathVariable Long paxId,
            @RequestBody TourPaxUpdateRequestDTO request) {
        return ResponseEntity.ok(tourPaxService.updateTourPaxConfiguration(tourId, paxId, request));
    }

    @DeleteMapping("/{paxId}")
    public ResponseEntity<GeneralResponse<String>> deleteTourPaxConfiguration(
            @PathVariable Long tourId,
            @PathVariable Long paxId) {
        return ResponseEntity.ok(tourPaxService.deleteTourPaxConfiguration(tourId, paxId));
    }

    @GetMapping("/markup")
    public ResponseEntity<GeneralResponse<TourMarkupResponseDTO>> getTourMarkupPercentage(
            @PathVariable Long tourId) {
        return ResponseEntity.ok(tourService.getTourMarkupPercentage(tourId));
    }

    @PutMapping("/update-markup")
    public ResponseEntity<GeneralResponse<TourResponseDTO>> updateTourMarkupPercentage(
            @PathVariable Long tourId,
            @RequestBody TourMarkupUpdateRequestDTO request) {
        return ResponseEntity.ok(tourService.updateTourMarkupPercentage(tourId, request.getMarkUpPercent()));
    }
}