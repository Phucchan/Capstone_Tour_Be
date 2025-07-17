package com.fpt.capstone.tourism.helper;

import com.fpt.capstone.tourism.helper.IHelper.TourHelper;
import com.fpt.capstone.tourism.repository.TourManagementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class TourHelperImpl implements TourHelper {
    private static final SecureRandom random = new SecureRandom();
    private final TourManagementRepository tourRepository;

    @Override
    public String generateTourCode() {
        String code;
        do {
            int number = 100 + random.nextInt(900);
            code = "DIDAU" + number;
        } while (tourRepository.existsByCode(code));
        return code;
    }
}