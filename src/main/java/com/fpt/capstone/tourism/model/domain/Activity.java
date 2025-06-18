package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

@Data
public class Activity {
    private int id;
    private String title;
    private String content;
    private String category;
    private String duration;
    private String imageUrl;
}
