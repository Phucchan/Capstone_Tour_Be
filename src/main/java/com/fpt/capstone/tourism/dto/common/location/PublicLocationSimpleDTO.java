package com.fpt.capstone.tourism.dto.common.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicLocationSimpleDTO {
    private Long id;
    private String name;
//    private Long geoPositionId;
//    private Double latitude;
//    private Double longitude;
}
