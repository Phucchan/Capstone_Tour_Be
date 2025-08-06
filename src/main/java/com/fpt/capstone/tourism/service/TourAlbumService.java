package com.fpt.capstone.tourism.service;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.TourPhotoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TourAlbumService {
    GeneralResponse<List<BookingSummaryDTO>> getCompletedTours(Long userId);

    GeneralResponse<List<TourPhotoDTO>> getPhotos(Long userId, Long bookingId);

    GeneralResponse<TourPhotoDTO> addPhoto(Long userId, Long bookingId, MultipartFile file);

    GeneralResponse<String> deletePhoto(Long userId, Long photoId);
}