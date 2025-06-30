package com.fpt.capstone.tourism.dto.response.analytic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyNewUserDTO {
    private int year;
    private int month;
    private Long userCount;
}
