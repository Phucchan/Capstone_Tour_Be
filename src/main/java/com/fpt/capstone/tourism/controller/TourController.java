package com.fpt.capstone.tourism.controller;

import com.fpt.capstone.tourism.dto.common.location.LocationDTO;
import com.fpt.capstone.tourism.dto.common.location.LocationShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.homepage.SaleTourDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.SearchTourResponseDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourDetailDTO;
import com.fpt.capstone.tourism.dto.response.tour.TourLocationOptionsDTO;
import com.fpt.capstone.tourism.service.LocationService;
import com.fpt.capstone.tourism.service.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/tours")
@RequiredArgsConstructor
public class TourController {
    private final TourService tourService;
    private final LocationService locationService;

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

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<TourDetailDTO>> getTourDetailById(@PathVariable Long id) {
        TourDetailDTO result = tourService.getTourDetailById(id);
        return ResponseEntity.ok(GeneralResponse.of(result, "Tour detail loaded successfully."));
    }

    @GetMapping("/search")
    // postman http://localhost:8080/v1/public/tours/search?priceMin=1000000&priceMax=5000000&departId=1&destId=2&date=2023-10-01&page=0&size=12&sortField=createdAt&sortDirection=desc
    public ResponseEntity<GeneralResponse<SearchTourResponseDTO>> searchTours(
            // Các tham số cho việc lọc, không bắt buộc
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) Long departId,
            @RequestParam(required = false) Long destId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            // Các tham số cho việc phân trang
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        PagingDTO<TourSummaryDTO> result = tourService.searchTours(priceMin, priceMax, departId, destId, date, pageable);
        List<LocationShortDTO> departures = locationService.getAllDepartures().stream()
                .map(d -> LocationShortDTO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .build())
                .collect(Collectors.toList());
        List<LocationShortDTO> destinations = locationService.getAllDestinations().stream()
                .map(d -> LocationShortDTO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .build())
                .collect(Collectors.toList());
        TourLocationOptionsDTO options = TourLocationOptionsDTO.builder()
                .departures(departures)
                .destinations(destinations)
                .build();

        SearchTourResponseDTO dto = SearchTourResponseDTO.builder()
                .tours(result)
                .options(options)
                .build();

        return ResponseEntity.ok(GeneralResponse.of(dto, "Tours filtered successfully."));
    }


    @GetMapping("/discounts")
    //postman http://localhost:8080/v1/public/tours/discounts?page=0&size=6
    public ResponseEntity<GeneralResponse<PagingDTO<SaleTourDTO>>> getDiscountTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "discountPercent"));

        PagingDTO<SaleTourDTO> result = tourService.getDiscountTours(pageable);
        return ResponseEntity.ok(GeneralResponse.of(result, "Discount tours loaded successfully."));
    }
    @GetMapping("/destinations/{destId}/search")
    //postman http://localhost:8080/v1/public/tours/destinations/1/search?page=0&size=6&sortField=createdAt&sortDirection=desc
    public ResponseEntity<GeneralResponse<SearchTourResponseDTO>> searchToursByDestination(
            @PathVariable Long destId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        PagingDTO<TourSummaryDTO> result = tourService.searchTours(null, null, null, destId, null, pageable);
        List<LocationShortDTO> departures = locationService.getAllDepartures().stream()
                .map(d -> LocationShortDTO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .build())
                .collect(Collectors.toList());
        List<LocationShortDTO> destinations = locationService.getAllDestinations().stream()
                .map(d -> LocationShortDTO.builder()
                        .id(d.getId())
                        .name(d.getName())
                        .build())
                .collect(Collectors.toList());
        TourLocationOptionsDTO options = TourLocationOptionsDTO.builder()
                .departures(departures)
                .destinations(destinations)
                .build();

        SearchTourResponseDTO dto = SearchTourResponseDTO.builder()
                .tours(result)
                .options(options)
                .build();

        return ResponseEntity.ok(GeneralResponse.of(dto, "Tours by destination loaded successfully."));
    }
}


