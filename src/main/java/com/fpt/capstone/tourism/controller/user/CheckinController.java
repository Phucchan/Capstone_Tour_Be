package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.TourPhotoDTO;
import com.fpt.capstone.tourism.service.TourAlbumService;
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

    private final TourAlbumService tourAlbumService;

    @GetMapping
    public ResponseEntity<GeneralResponse<List<BookingSummaryDTO>>> getCompletedTours(@PathVariable Long userId) {
        return ResponseEntity.ok(tourAlbumService.getCompletedTours(userId));
    }

    @GetMapping("/{bookingId}/photos")
    public ResponseEntity<GeneralResponse<List<TourPhotoDTO>>> getPhotos(@PathVariable Long userId,
                                                                         @PathVariable Long bookingId) {
        return ResponseEntity.ok(tourAlbumService.getPhotos(userId, bookingId));
    }

    @PostMapping(value = "/{bookingId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<TourPhotoDTO>> addPhoto(@PathVariable Long userId,
                                                                  @PathVariable Long bookingId,
                                                                  @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(tourAlbumService.addPhoto(userId, bookingId, file));
    }

    @DeleteMapping("/photos/{photoId}")
    public ResponseEntity<GeneralResponse<String>> deletePhoto(@PathVariable Long userId,
                                                               @PathVariable Long photoId) {
        return ResponseEntity.ok(tourAlbumService.deletePhoto(userId, photoId));
    }
}