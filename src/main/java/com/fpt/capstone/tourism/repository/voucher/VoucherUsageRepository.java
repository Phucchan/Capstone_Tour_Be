package com.fpt.capstone.tourism.repository.voucher;

import com.fpt.capstone.tourism.model.voucher.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
}
