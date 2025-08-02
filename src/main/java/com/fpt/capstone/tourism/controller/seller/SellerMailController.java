package com.fpt.capstone.tourism.controller.seller;

import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.request.seller.SellerMailRequestDTO;
import com.fpt.capstone.tourism.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seller/mail")
public class SellerMailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<GeneralResponse<String>> sendMail(@RequestBody SellerMailRequestDTO request) {
        emailService.sendEmail(request.getEmail(), request.getSubject(), request.getContent());
        return ResponseEntity.ok(GeneralResponse.of("success"));
    }
}
