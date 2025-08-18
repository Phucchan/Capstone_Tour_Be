package com.fpt.capstone.tourism.repository.user;

import com.fpt.capstone.tourism.model.voucher.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {
    List<UserVoucher> findByUserId(Long userId);
}
