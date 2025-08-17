package com.fpt.capstone.tourism.controller.user;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;
import com.fpt.capstone.tourism.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    //postman http://localhost:8080/v1/customer/vouchers?page=1&size=10&keyword=discount
    public ResponseEntity<GeneralResponse<PagingDTO<VoucherSummaryDTO>>> getAvailableVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(voucherService.getAvailableVouchers(keyword, page, size));
    }

    @PostMapping("/{voucherId}/redeem")
    // postman http://localhost:8080/v1/customer/vouchers/1/redeem?userId=123

    public ResponseEntity<GeneralResponse<String>> redeemVoucher(@RequestParam Long userId,
                                                                 @PathVariable Long voucherId) {
        return ResponseEntity.ok(voucherService.redeemVoucher(userId, voucherId));
    }
}
