package com.fpt.capstone.tourism.controller;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.response.homepage.HomepageDataDTO;
import com.fpt.capstone.tourism.service.HomepageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/homepage")
@RequiredArgsConstructor
public class HomepageController {
    private final HomepageService homepageService;

    @GetMapping
    //postman http://localhost:8080/v1/public/homepage
    public ResponseEntity<GeneralResponse<HomepageDataDTO>> getHomepageData() {
        HomepageDataDTO data = homepageService.getHomepageData();
        return ResponseEntity.ok(GeneralResponse.of(data, "Homepage data loaded successfully"));
    }
}
