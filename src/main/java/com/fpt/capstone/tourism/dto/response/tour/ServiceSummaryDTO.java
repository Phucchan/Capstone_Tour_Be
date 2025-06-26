package com.fpt.capstone.tourism.dto.response.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSummaryDTO {
    private String name;
    private String type; // Ví dụ: "Khách sạn", "Nhà hàng"
    private String imageUrl;
}