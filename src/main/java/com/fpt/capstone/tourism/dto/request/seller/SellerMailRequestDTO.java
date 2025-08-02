package com.fpt.capstone.tourism.dto.request.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerMailRequestDTO {
    private String email;
    private String subject;
    private String content;
}