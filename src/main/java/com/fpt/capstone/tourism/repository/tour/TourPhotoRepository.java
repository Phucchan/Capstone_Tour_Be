package com.fpt.capstone.tourism.repository.tour;

import com.fpt.capstone.tourism.model.tour.TourPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourPhotoRepository extends JpaRepository<TourPhoto, Long> {
    List<TourPhoto> findByBooking_Id(Long bookingId);
    Optional<TourPhoto> findByIdAndBooking_User_Id(Long id, Long userId);
}