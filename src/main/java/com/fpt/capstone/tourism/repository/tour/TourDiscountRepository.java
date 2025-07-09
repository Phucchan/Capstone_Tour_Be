package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourDiscountRepository extends JpaRepository<TourDiscount, Long> {
}