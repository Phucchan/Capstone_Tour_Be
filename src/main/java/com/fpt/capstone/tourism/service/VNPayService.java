package com.fpt.capstone.tourism.service;

import jakarta.servlet.http.HttpServletRequest;

public interface VNPayService {
    String generatePaymentUrl(double total, String orderInformation, String urlReturn, int minuteExpire);

    int orderReturn(HttpServletRequest request);
}
