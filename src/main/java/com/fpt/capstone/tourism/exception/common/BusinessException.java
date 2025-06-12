package com.fpt.capstone.tourism.exception.common;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessException extends RuntimeException {
    protected int httpCode;
    protected String responseMessage;
    protected Object responseData;

    public static BusinessException of(String responseMessage, Exception e) {
        return BusinessException.builder()
                .httpCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .responseMessage(responseMessage)
                .responseData(e.getLocalizedMessage())
                .build();
    }

    public static BusinessException of(String responseMessage) {
        return BusinessException.builder()
                .httpCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .responseMessage(responseMessage)
                .responseData(null)
                .build();
    }

    @Override
    public String getMessage() {
        return responseMessage;
    }

    public static BusinessException of(HttpStatus status, String responseMessage) {
        return BusinessException.builder()
                .httpCode(status.value())
                .responseMessage(responseMessage)
                .responseData(null)
                .build();
    }

    public static BusinessException of(HttpStatus status, String responseMessage,Exception e) {
        return BusinessException.builder()
                .httpCode(status.value())
                .responseMessage(responseMessage)
                .responseData(e.getLocalizedMessage())
                .build();
    }

    public static BusinessException of(HttpStatus status, String responseMessage, Object responseData) {
        return BusinessException.builder()
                .httpCode(status.value())
                .responseMessage(responseMessage)
                .responseData(responseData)
                .build();
    }
}