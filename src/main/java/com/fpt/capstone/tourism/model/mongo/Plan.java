package com.fpt.capstone.tourism.model.mongo;

import com.fpt.capstone.tourism.model.domain.PlanDay;
import com.fpt.capstone.tourism.model.enums.PlanStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "plans")
@Data
public class Plan {
    @Id
    private String id;
    private int numberDays;
    private String title;
    private String planCategory;
    private String thumbnailImageUrl;
    private String location;
    private int locationId;
    private String preferences;
    private String description;
    private List<PlanDay> days;
    private String adminId;
    private List<String> memberIds;
    private LocalDateTime createdAt;
    private PlanStatus planStatus;
}
