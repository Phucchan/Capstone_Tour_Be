package com.fpt.capstone.tourism.dto.response.tour;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private int rating;
    private String comment;
    private String userName;
    private String userAvatarUrl;
    private LocalDateTime createdAt;
}
