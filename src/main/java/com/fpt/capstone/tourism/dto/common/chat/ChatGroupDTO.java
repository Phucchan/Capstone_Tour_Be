package com.fpt.capstone.tourism.dto.common.chat;


import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ChatGroupDTO {
    private Long id;
    private String name;
    private boolean grouped;
    private Long userId;
}
