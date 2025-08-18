package com.fpt.capstone.tourism.model.mongo;

import com.fpt.capstone.tourism.model.domain.PlanDay;
import com.fpt.capstone.tourism.model.domain.Transport;
import com.fpt.capstone.tourism.model.enums.PlanStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "plans")
@Data
@Builder
public class Plan {
    @Id
    private String id;
    private int numberDays;
    private String title;
    private String description;
    private String thumbnailImageUrl;
    private double totalSpend;
    private String planType;
    private List<String> preferences;
    private List<PlanDay> days;
    private int creatorId;
    private LocalDateTime createdAt;
    private PlanStatus planStatus;
    private List<Transport> transports;
}
