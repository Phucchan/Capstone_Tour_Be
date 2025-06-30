package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.WishlistRequestDTO;
import com.fpt.capstone.tourism.dto.response.homepage.TourSummaryDTO;
import com.fpt.capstone.tourism.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<GeneralResponse<String>> addWishlist(@RequestBody WishlistRequestDTO request) {
        return ResponseEntity.ok(wishlistService.addToWishlist(request.getUserId(), request.getTourId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<String>> deleteWishlist(@PathVariable("id") Long id,
                                                                  @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(wishlistService.deleteWishlist(id, userId));
    }
    //api postman: http://localhost:8080/public/wishlists/user/1
    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse<List<TourSummaryDTO>>> getWishlist(@PathVariable Long userId) {
        return ResponseEntity.ok(wishlistService.getWishlist(userId));
    }
}