package com.fpt.capstone.tourism.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.capstone.tourism.helper.IHelper.AiPlanParserHelper;
import org.springframework.stereotype.Service;

@Service
public class AiPlanParserHelperImpl implements AiPlanParserHelper {

    @Override
    public double extractTotalSpend(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            if (root.has("totalSpend")) {
                return root.get("totalSpend").asDouble();
            } else {
                throw new IllegalArgumentException("Không tìm thấy trường 'totalSpend' trong JSON.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi trích xuất totalSpend từ JSON: " + e.getMessage(), e);
        }
    }
}
