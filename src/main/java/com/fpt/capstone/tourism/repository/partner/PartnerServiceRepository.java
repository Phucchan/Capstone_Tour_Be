package com.fpt.capstone.tourism.repository.partner;

import com.fpt.capstone.tourism.model.domain.projection.PartnerServiceWithDayDTO;
import com.fpt.capstone.tourism.model.enums.PartnerServiceStatus;
import com.fpt.capstone.tourism.model.partner.PartnerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartnerServiceRepository extends JpaRepository<PartnerService, Long> {


    @Query(value = """
        SELECT s.service_id AS serviceId, td.day_number AS dayNumber
        FROM tour_schedules ts
        JOIN tours t ON ts.tour_id = t.tour_id
        JOIN tour_day td ON td.tour_id = t.tour_id
        JOIN tour_day_services tds ON tds.tour_day_id = td.id
        JOIN services s ON s.service_id = tds.service_id
        WHERE ts.schedule_id = :scheduleId
    """, nativeQuery = true)
    List<PartnerServiceWithDayDTO> findServicesWithDayNumberByScheduleId(@Param("scheduleId") Long scheduleId);

    @Query("SELECT ps FROM PartnerService ps WHERE (:serviceTypeId IS NULL OR ps.serviceType.id = :serviceTypeId) " +
            "AND (:locationId IS NULL OR ps.partner.location.id = :locationId)")
    List<PartnerService> findByServiceTypeAndLocation(@Param("serviceTypeId") Long serviceTypeId,
                                                      @Param("locationId") Long locationId);

    @Query("SELECT ps FROM PartnerService ps WHERE ps.deleted = FALSE")
    List<PartnerService> findByDeletedFalse();

        // Tìm tất cả dịch vụ theo ID của loại dịch vụ
        List<PartnerService> findByServiceTypeId(Long serviceTypeId);

    Page<PartnerService> findByStatus(PartnerServiceStatus status, Pageable pageable);
    Page<PartnerService> findByStatusAndNameContainingIgnoreCase(PartnerServiceStatus status, String name, Pageable pageable);

    Page<PartnerService> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<PartnerService> findByStatusNot(PartnerServiceStatus status, Pageable pageable);

    Page<PartnerService> findByStatusNotAndNameContainingIgnoreCase(PartnerServiceStatus status, String name, Pageable pageable);
}