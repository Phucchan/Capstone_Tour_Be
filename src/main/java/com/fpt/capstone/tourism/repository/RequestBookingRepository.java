package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.RequestBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RequestBookingRepository extends JpaRepository<RequestBooking, Long> {
    Page<RequestBooking> findByUser_Id(Long userId, Pageable pageable);

    @Query("SELECT r FROM RequestBooking r WHERE r.user.id = :userId AND " +
            "(LOWER(r.tourTheme) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.destinationDetail) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<RequestBooking> searchByUserAndKeyword(@Param("userId") Long userId,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);
}