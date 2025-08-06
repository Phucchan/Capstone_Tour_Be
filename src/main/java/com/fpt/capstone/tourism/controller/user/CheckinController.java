package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.CheckInDTO;
import com.fpt.capstone.tourism.service.CheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/users/{userId}/checkin")
public class CheckinController {

    private final CheckInService checkInService;

    @GetMapping
    public ResponseEntity<GeneralResponse<List<BookingSummaryDTO>>> getCompletedTours(@PathVariable Long userId) {
        return ResponseEntity.ok(checkInService.getCompletedTours(userId));
    }

    @GetMapping("/{bookingId}/photos")
    public ResponseEntity<GeneralResponse<List<CheckInDTO>>> getCheckIns(@PathVariable Long userId,
                                                                         @PathVariable Long bookingId) {
        return ResponseEntity.ok(checkInService.getCheckIns(userId, bookingId));
    }

    @PostMapping(value = "/{bookingId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<CheckInDTO>> addCheckIn(@PathVariable Long userId,
                                                                  @PathVariable Long bookingId,
                                                                  @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(checkInService.addCheckIn(userId, bookingId, file));
    }
    @DeleteMapping("/photos/{checkInId}")
    public ResponseEntity<GeneralResponse<String>> deleteCheckIn(@PathVariable Long userId,
                                                                 @PathVariable Long checkInId) {
        return ResponseEntity.ok(checkInService.deleteCheckIn(userId, checkInId));
        }
    }