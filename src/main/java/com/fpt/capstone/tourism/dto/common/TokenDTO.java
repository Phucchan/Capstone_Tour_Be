package com.fpt.capstone.tourism.dto.common;

import com.fpt.capstone.tourism.dto.response.UserBasicDTO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {
    private UserBasicDTO user;
    private String token;
    private String expirationTime;
    private String redirectUrl;
}
