package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.WishlistTourSummaryDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/customer/{userId}/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{tourId}")
    public ResponseEntity<GeneralResponse<String>> addWishlist(@PathVariable Long userId,
                                                               @PathVariable Long tourId) {
        return ResponseEntity.ok(wishlistService.addToWishlist(userId, tourId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteWishlist(@PathVariable Long userId,
                                                                  @PathVariable("id") Long id) {
        return ResponseEntity.ok(wishlistService.deleteWishlist(id, userId));
    }
    @GetMapping
    public ResponseEntity<GeneralResponse<List<WishlistTourSummaryDTO>>> getWishlist(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }
}