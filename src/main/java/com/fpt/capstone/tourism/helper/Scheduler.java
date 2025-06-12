package com.fpt.capstone.tourism.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Scheduler {

    @Scheduled(fixedRate = 2 * 60 * 60 * 1000)
    void removeExpiredUnpaidBookings() {

    }
}
