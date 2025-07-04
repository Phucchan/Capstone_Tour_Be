package com.fpt.capstone.tourism.repository;

import com.fpt.capstone.tourism.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {
    Location findByName(String name);
    @Query(value = "SELECT * FROM location WHERE is_deleted = FALSE ORDER BY RANDOM() LIMIT :numberLocation", nativeQuery = true)
    List<Location> findRandomLocation(@Param("numberLocation") int numberLocation);
    List<Location> findByNameContainingIgnoreCase(String name);
    Page<Location> findByNameContainingIgnoreCase(String name, Pageable pageable);
}