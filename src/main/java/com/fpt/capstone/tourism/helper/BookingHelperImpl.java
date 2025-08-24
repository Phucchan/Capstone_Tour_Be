package com.fpt.capstone.tourism.helper;

import com.fpt.capstone.tourism.helper.IHelper.BookingHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingHelperImpl implements BookingHelper {

    @Override
    public String generateBookingCode(Long tourId, Long scheduleId, Long customerId) {
        // Get current date in DDMMYY format
        String datePart = new SimpleDateFormat("ddMMyy").format(new Date());


        String millisPart = String.valueOf(System.currentTimeMillis() % 1000);

        // Construct the booking code
        return String.format("%sT%d%s-%s", datePart, tourId, millisPart);
    }

    @Override
    public String generateBookingCode(Long tourId, String tourCode) {
        // Get current date in DDMMYY format
        String datePart = new SimpleDateFormat("ddMMyy").format(new Date());


        String millisPart = String.valueOf(System.currentTimeMillis() % 1000);

        // Construct the booking code
        return String.format("%sT%d%s-%s", datePart, tourId, tourCode, millisPart);
    }


}
