package com.fpt.capstone.tourism.dto.common.location;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationShortDTO {
    private Long id;
    private String name;
}
