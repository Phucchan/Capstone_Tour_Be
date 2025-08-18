package com.fpt.capstone.tourism.repository.voucher;

import com.fpt.capstone.tourism.model.enums.VoucherStatus;
import com.fpt.capstone.tourism.model.voucher.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    Page<Voucher> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<Voucher> findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(String code, Pageable pageable);

    @Query("SELECT v FROM Voucher v WHERE (v.deleted IS NULL OR v.deleted = false) " +
            "AND v.voucherStatus = :status " +
            "AND (v.validFrom IS NULL OR v.validFrom <= :now) " +
            "AND (v.validTo IS NULL OR v.validTo >= :now) " +
            "AND (v.maxUsage IS NULL OR v.maxUsage > 0) " +
            "AND (:keyword IS NULL OR LOWER(v.code) LIKE :keyword) " +
            "ORDER BY v.createdAt DESC")
    Page<Voucher> findAvailableVouchers(@Param("keyword") String keyword,
                                        @Param("status") VoucherStatus status,
                                        @Param("now") LocalDateTime now,
                                        Pageable pageable);
}