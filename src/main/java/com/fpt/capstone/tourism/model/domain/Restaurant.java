package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Restaurant {
    private int id;
    private String name;
    private String address;
    private String imageUrl;
    private List<String> menuItems;
    private LocalDateTime useDate;
}
