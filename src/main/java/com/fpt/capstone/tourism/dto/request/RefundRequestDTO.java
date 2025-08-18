package com.fpt.capstone.tourism.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDTO {
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String bankName;
}
