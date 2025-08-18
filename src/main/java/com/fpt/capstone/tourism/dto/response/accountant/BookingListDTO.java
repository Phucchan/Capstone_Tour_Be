package com.fpt.capstone.tourism.dto.response.accountant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListDTO {
    private int stt;
    private Long bookingId;
    private String tourName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String tourType;
    private int duration;
    private String status;
}
