package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.TourPhotoDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.model.tour.TourPhoto;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.tour.TourPhotoRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.S3Service;
import com.fpt.capstone.tourism.service.TourAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TourAlbumServiceImpl implements TourAlbumService {

    private final BookingRepository bookingRepository;
    private final TourPhotoRepository tourPhotoRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket-url}")
    private String bucketUrl;

    @Override
    public GeneralResponse<List<BookingSummaryDTO>> getCompletedTours(Long userId) {
        try {
            userRepository.findUserById(userId)
                    .orElseThrow(() -> BusinessException.of(Constants.UserExceptionInformation.USER_NOT_FOUND));

            List<Booking> bookings = bookingRepository.findByUser_IdAndBookingStatus(userId, BookingStatus.COMPLETED);

            List<BookingSummaryDTO> dtos = bookings.stream()
                    .map(b -> BookingSummaryDTO.builder()
                            .id(b.getId())
                            .bookingCode(b.getBookingCode())
                            .tourName(b.getTourSchedule().getTour().getName())
                            .status(b.getBookingStatus() != null ? b.getBookingStatus().name() : null)
                            .totalAmount(b.getTotalAmount())
                            .createdAt(b.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());

            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.GET_BOOKING_LIST_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.GET_BOOKING_LIST_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<List<TourPhotoDTO>> getPhotos(Long userId, Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.BOOKING_NOT_FOUND));
            if (!booking.getUser().getId().equals(userId) || booking.getBookingStatus() != BookingStatus.COMPLETED) {
                throw BusinessException.of(Constants.Message.NO_PERMISSION_TO_ACCESS);
            }

            List<TourPhoto> photos = tourPhotoRepository.findByBooking_Id(bookingId);
            List<TourPhotoDTO> dtos = photos.stream()
                    .map(p -> TourPhotoDTO.builder()
                            .id(p.getId())
                            .imageUrl(p.getImageUrl())
                            .createdAt(p.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.GET_TOUR_PHOTOS_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.GET_TOUR_PHOTOS_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<TourPhotoDTO> addPhoto(Long userId, Long bookingId, MultipartFile file) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.BOOKING_NOT_FOUND));
            if (!booking.getUser().getId().equals(userId) || booking.getBookingStatus() != BookingStatus.COMPLETED) {
                throw BusinessException.of(Constants.Message.NO_PERMISSION_TO_ACCESS);
            }

            if (file == null || file.isEmpty()) {
                throw BusinessException.of(Constants.Message.ADD_TOUR_PHOTO_FAIL);
            }

            String key = s3Service.uploadFile(file, "albums");

            TourPhoto photo = TourPhoto.builder()
                    .booking(booking)
                    .imageUrl(bucketUrl + "/" + key)
                    .build();
            TourPhoto saved = tourPhotoRepository.save(photo);

            TourPhotoDTO dto = TourPhotoDTO.builder()
                    .id(saved.getId())
                    .imageUrl(saved.getImageUrl())
                    .createdAt(saved.getCreatedAt())
                    .build();

            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.ADD_TOUR_PHOTO_SUCCESS, dto);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.ADD_TOUR_PHOTO_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<String> deletePhoto(Long userId, Long photoId) {
        try {
            TourPhoto photo = tourPhotoRepository.findByIdAndBooking_User_Id(photoId, userId)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.TOUR_PHOTO_NOT_FOUND));
            String key = photo.getImageUrl().replace(bucketUrl + "/", "");
            s3Service.deleteFile(key);
            tourPhotoRepository.delete(photo);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.DELETE_TOUR_PHOTO_SUCCESS, null);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.DELETE_TOUR_PHOTO_FAIL, ex);
        }
    }
}
