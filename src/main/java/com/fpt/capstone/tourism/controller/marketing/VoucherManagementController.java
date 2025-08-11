package com.fpt.capstone.tourism.controller.marketing;

import com.fpt.capstone.tourism.dto.common.VoucherDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.VoucherRequestDTO;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.response.VoucherSummaryDTO;
import com.fpt.capstone.tourism.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/marketing/vouchers")
public class VoucherManagementController {

    private final VoucherService voucherService;

    @PostMapping
    //postman http://localhost:8080/v1/marketing/vouchers
    public ResponseEntity<GeneralResponse<VoucherDTO>> createVoucher(@RequestBody VoucherRequestDTO requestDTO) {
        return ResponseEntity.ok(voucherService.createVoucher(requestDTO));
    }

    @GetMapping
    //postman http://localhost:8080/v1/marketing/vouchers?page=0&size=10&keyword=DISCOUNT
    public ResponseEntity<GeneralResponse<PagingDTO<VoucherSummaryDTO>>> getVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(voucherService.getVouchers(keyword, page, size));
    }
}