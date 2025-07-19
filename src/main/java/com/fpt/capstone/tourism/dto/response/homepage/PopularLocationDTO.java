package com.fpt.capstone.tourism.dto.response.homepage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopularLocationDTO {
    private Long id;
    private String name;
    private String image;
}
