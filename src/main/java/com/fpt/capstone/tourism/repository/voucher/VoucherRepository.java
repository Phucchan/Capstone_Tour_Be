package com.fpt.capstone.tourism.repository.voucher;

import com.fpt.capstone.tourism.model.voucher.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByCode(String code);

    Page<Voucher> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<Voucher> findByDeletedFalseAndCodeContainingIgnoreCaseOrderByCreatedAtDesc(String code, Pageable pageable);
}