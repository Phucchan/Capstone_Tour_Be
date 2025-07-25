package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserIdAndTourId(Long userId, Long tourId);
    List<Wishlist> findByUserId(Long userId);
}