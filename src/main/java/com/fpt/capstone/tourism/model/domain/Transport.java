package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

@Data
public class Transport {
    private int id;
    private String name;
    private String websiteUrl;
    private String logoUrl;
    private double estimatedCost;
}
