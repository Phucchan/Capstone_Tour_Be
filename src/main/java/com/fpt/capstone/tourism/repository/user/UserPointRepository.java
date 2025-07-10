package com.fpt.capstone.tourism.repository.user;

import com.fpt.capstone.tourism.model.UserPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointRepository extends JpaRepository<UserPoint, Long> {
    @Query("SELECT COALESCE(SUM(up.points),0) FROM UserPoint up WHERE up.user.id = :userId")
    Integer sumPointsByUserId(@Param("userId") Long userId);
}
