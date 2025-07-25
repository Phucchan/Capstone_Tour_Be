package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.TourMapper;
import com.fpt.capstone.tourism.model.User;
import com.fpt.capstone.tourism.model.tour.Tour;
import com.fpt.capstone.tourism.model.Wishlist;
import com.fpt.capstone.tourism.repository.user.UserRepository;
import com.fpt.capstone.tourism.repository.tour.FeedbackRepository;
import com.fpt.capstone.tourism.repository.tour.TourPaxRepository;
import com.fpt.capstone.tourism.repository.tour.TourRepository;
import com.fpt.capstone.tourism.repository.WishlistRepository;
import com.fpt.capstone.tourism.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final FeedbackRepository feedbackRepository;
    private final TourPaxRepository tourPaxRepository;

    @Override
    public GeneralResponse<String> addToWishlist(Long userId, Long tourId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> BusinessException.of(Constants.UserExceptionInformation.USER_NOT_FOUND));
            Tour tour = tourRepository.findById(tourId)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.TOUR_NOT_FOUND));

            if (wishlistRepository.findByUserIdAndTourId(userId, tourId).isPresent()) {
                return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.CREATE_WISHLIST_SUCCESS, null);
            }

            Wishlist wishlist = Wishlist.builder()
                    .user(user)
                    .tour(tour)
                    .build();
            wishlistRepository.save(wishlist);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.CREATE_WISHLIST_SUCCESS, null);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.CREATE_WISHLIST_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<String> deleteWishlist(Long wishlistId, Long userId) {
        try {
            Wishlist wishlist = wishlistRepository.findById(wishlistId)
                    .orElseThrow(() -> BusinessException.of(Constants.Message.WISHLIST_NOT_FOUND));
            if (!wishlist.getUser().getId().equals(userId)) {
                throw BusinessException.of(Constants.Message.NO_PERMISSION_TO_DELETE);
            }
            wishlistRepository.delete(wishlist);
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.DELETE_WISHLIST_SUCCESS, null);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.DELETE_WISHLIST_FAIL, ex);
        }
    }

    @Override
    public GeneralResponse<List<TourSummaryDTO>> getWishlist(Long userId) {
        try {
            List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
            List<TourSummaryDTO> dtos = wishlists.stream()
                    .map(w -> {
                        Tour tour = w.getTour();
                        TourSummaryDTO dto = tourMapper.tourToTourSummaryDTO(tour);
                        Double rating = feedbackRepository.findAverageRatingByTourId(tour.getId());
                        Double startingPrice = tourPaxRepository.findStartingPriceByTourId(tour.getId());
                        dto.setAverageRating(rating);
                        dto.setStartingPrice(startingPrice);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return new GeneralResponse<>(HttpStatus.OK.value(), Constants.Message.WISHLIST_LOAD_SUCCESS, dtos);
        } catch (BusinessException be) {
            throw be;
        } catch (Exception ex) {
            throw BusinessException.of(Constants.Message.WISHLIST_LOAD_FAIL, ex);
        }
    }
}

