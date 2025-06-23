package com.fpt.capstone.tourism.controller;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/public/tours")
@RequiredArgsConstructor
public class TourController {
    private final TourService tourService;

    @GetMapping("/fixed")
    public ResponseEntity<GeneralResponse<PagingDTO<TourSummaryDTO>>> getFixedTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        PagingDTO<TourSummaryDTO> result = tourService.getFixedTours(pageable);
        return ResponseEntity.ok(GeneralResponse.of(result, "Fixed tours loaded successfully."));
    }

    @GetMapping("/locations/{locationId}/tours")
    public ResponseEntity<GeneralResponse<PagingDTO<TourSummaryDTO>>> getToursByLocation(
            @PathVariable Long locationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size, // Sửa lại size mặc định là 6
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        PagingDTO<TourSummaryDTO> result = tourService.getToursByLocation(locationId, pageable);
        return ResponseEntity.ok(GeneralResponse.of(result, "Tours by location loaded successfully."));
    }


}
