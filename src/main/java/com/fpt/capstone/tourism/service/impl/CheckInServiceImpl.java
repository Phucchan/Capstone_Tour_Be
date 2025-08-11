package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.BookingSummaryDTO;
import com.fpt.capstone.tourism.dto.response.tour.CheckInDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.model.UserPoint;
import com.fpt.capstone.tourism.model.enums.BookingStatus;
import com.fpt.capstone.tourism.model.tour.Booking;
import com.fpt.capstone.tourism.model.tour.CheckIn;
import com.fpt.capstone.tourism.repository.booking.BookingRepository;
import com.fpt.capstone.tourism.repository.tour.CheckInRepository;
import com.fpt.capstone.tourism.repository.user.UserPointRepository;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.service.CheckInService;
import com.fpt.capstone.tourism.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
    public class CheckInServiceImpl implements CheckInService {

        private final BookingRepository bookingRepository;
        private final CheckInRepository checkInRepository;
        private final UserRepository userRepository;
        private final UserPointRepository userPointRepository;
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
                                .tourId(b.getTourSchedule().getTour().getId())
                                .bookingCode(b.getBookingCode())
                                .tourName(b.getTourSchedule().getTour().getName())
                                .status(b.getBookingStatus() != null ? b.getBookingStatus().name() : null)
                                .totalAmount(b.getTotalAmount())
                                .createdAt(b.getCreatedAt())
                                .departureDate(b.getTourSchedule().getDepartureDate())
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
            public GeneralResponse<List<CheckInDTO>> getCheckIns(Long userId, Long bookingId) {
                try {
                    Booking booking = bookingRepository.findById(bookingId)
                            .orElseThrow(() -> BusinessException.of(Constants.Message.BOOKING_NOT_FOUND));
                    if (!booking.getUser().getId().equals(userId) || booking.getBookingStatus() != BookingStatus.COMPLETED) {
                        throw BusinessException.of(Constants.Message.NO_PERMISSION_TO_ACCESS);
                    }

                    List<CheckIn> checkIns = checkInRepository.findByBooking_Id(bookingId);
                    List<CheckInDTO> dtos = checkIns.stream()
                            .map(c -> CheckInDTO.builder()
                                    .id(c.getId())
                                    .imageUrl(c.getImageUrl())
                                    .createdAt(c.getCreatedAt())
                                    .pointsEarned(c.getPointsEarned())
                                    .build())
                            .collect(Collectors.toList());
                    return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.GET_CHECKINS_SUCCESS, dtos);
                } catch (BusinessException be) {
                    throw be;
                } catch (Exception ex) {
                    throw BusinessException.of(Constants.Message.GET_CHECKINS_FAIL, ex);
                }
            }

            @Override
                public GeneralResponse<CheckInDTO> addCheckIn(Long userId, Long bookingId, MultipartFile file) {
                    try {
                        Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> BusinessException.of(Constants.Message.BOOKING_NOT_FOUND));
                        if (!booking.getUser().getId().equals(userId) || booking.getBookingStatus() != BookingStatus.COMPLETED) {
                            throw BusinessException.of(Constants.Message.NO_PERMISSION_TO_ACCESS);
                        }

                        if (file == null || file.isEmpty()) {
                            throw BusinessException.of(Constants.Message.ADD_CHECKIN_FAIL);
                        }

                        String key = s3Service.uploadFile(file, "albums");

                        long earned = checkInRepository.countByBooking_IdAndPointsEarnedGreaterThan(bookingId, 0);
                        int pointsAwarded = earned < 10 ? 1 : 0;

                        CheckIn checkIn = CheckIn.builder()
                                .booking(booking)
                                .imageUrl(bucketUrl + "/" + key)
                                .build();
                        CheckIn saved = checkInRepository.save(checkIn);

                        if (pointsAwarded > 0) {
                            UserPoint userPoint = UserPoint.builder()
                                    .user(booking.getUser())
                                    .points(pointsAwarded)
                                    .build();
                            userPointRepository.save(userPoint);
                        }

                        CheckInDTO dto = CheckInDTO.builder()
                                .id(saved.getId())
                                .imageUrl(saved.getImageUrl())
                                .pointsEarned(saved.getPointsEarned())
                                .createdAt(saved.getCreatedAt())
                                .build();

                        return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.ADD_CHECKIN_SUCCESS, dto);
                    } catch (BusinessException be) {
                        throw be;
                    } catch (Exception ex) {
                        throw BusinessException.of(Constants.Message.ADD_CHECKIN_FAIL, ex);
                    }
                }

                @Override
                    public GeneralResponse<String> deleteCheckIn(Long userId, Long checkInId) {
                        try {
                            CheckIn checkIn = checkInRepository.findByIdAndBooking_User_Id(checkInId, userId)
                                    .orElseThrow(() -> BusinessException.of(Constants.Message.CHECKIN_NOT_FOUND));
                            String key = checkIn.getImageUrl().replace(bucketUrl + "/", "");
                            s3Service.deleteFile(key);
                            checkInRepository.delete(checkIn);
                            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.DELETE_CHECKIN_SUCCESS, null);
                        } catch (BusinessException be) {
                            throw be;
                        } catch (Exception ex) {
                            throw BusinessException.of(Constants.Message.DELETE_CHECKIN_FAIL, ex);
                        }
                    }
                }
