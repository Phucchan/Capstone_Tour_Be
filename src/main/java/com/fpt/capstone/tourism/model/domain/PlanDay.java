package com.fpt.capstone.tourism.model.domain;

import lombok.Data;

import java.util.List;


@Data
public class PlanDay {
    private String title;
    private int dayNumber;
    private String date;
    private int totalSpend;
    private int locationId;
    private String locationName;
    private String longDescription;
    private List<Activity> activities;
    private List<Hotel> hotels;
    private List<Restaurant> restaurants;
}
