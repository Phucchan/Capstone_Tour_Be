package com.fpt.capstone.tourism.model.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDetail {
    private String title;
    private String description;
    private List<Transport> transports;
}
