package com.fpt.capstone.tourism.dto.response.tourManager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dùng để trả về thông tin chi tiết của tour kèm theo các tuỳ chọn
 * cần thiết cho giao diện quản lý.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourDetailOptionsDTO {
    private TourDetailManagerDTO detail;
    private TourOptionsDTO options;
}