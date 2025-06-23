package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourPax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourPaxRepository extends JpaRepository<TourPax, Long> {
    /**
     * Finds the lowest selling price for a given tour, which serves as the "starting from" price.
     * It orders by the minimum quantity to get the price for the smallest group first.
     * @param tourId The ID of the tour.
     * @return The lowest selling price.
     */
    @Query("SELECT tp.sellingPrice FROM TourPax tp WHERE tp.tour.id = :tourId ORDER BY tp.minQuantity ASC LIMIT 1")
    Double findStartingPriceByTourId(Long tourId);

    List<TourPax> findByTourId(Long tourId);
}
