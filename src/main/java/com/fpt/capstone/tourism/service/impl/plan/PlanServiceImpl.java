package com.fpt.capstone.tourism.service.impl.plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.partner.PartnerShortDTO;
import com.fpt.capstone.tourism.dto.request.plan.PlanDayDTO;
import com.fpt.capstone.tourism.dto.request.plan.PlanGenerationRequestDTO;
import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import com.fpt.capstone.tourism.enrich.ActivityImageEnricher;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.helper.IHelper.AiPlanParserHelper;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.domain.Activity;
import com.fpt.capstone.tourism.model.domain.PlanDay;
import com.fpt.capstone.tourism.model.domain.PlanDetail;
import com.fpt.capstone.tourism.model.enums.PlanStatus;
import com.fpt.capstone.tourism.model.mongo.Plan;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.mongo.PlanRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerRepository;
import com.fpt.capstone.tourism.service.GeminiApiService;
import com.fpt.capstone.tourism.service.SerpApiService;
import com.fpt.capstone.tourism.service.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {


    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final GeminiApiService geminiApiService;
    private final PartnerRepository partnerRepository;
    private final AiPlanParserHelper aiPlanParserHelper;
    private final ActivityImageEnricher activityImageEnricher;
    private final PlanRepository planRepository;

    @Override
    public List<PublicLocationDTO> getLocations() {
        try {
            List<Location> locations = locationRepository.findAllLocations();
            return locations.stream().map(locationMapper::toPublicLocationDTO).toList();
        } catch (Exception ex) {
            throw BusinessException.of("Không thể lấy địa điểm cho kế hoạch", ex);
        }
    }

    @Override
    public Plan generatePlan(PlanGenerationRequestDTO dto) {
        try {
            int totalDays = dto.getDays().size();
            String preferences = String.join(", ", dto.getPreferences());

            List<Integer> locationIds = dto.getDays().stream()
                    .map(PlanDayDTO::getLocationId)
                    .distinct()
                    .toList();

            List<String> locationNames = dto.getDays().stream()
                    .map(PlanDayDTO::getLocationName)
                    .distinct()
                    .toList();

            List<PartnerShortDTO> partners = partnerRepository.findAllShortByLocationIds(locationIds);

            if (partners.isEmpty()) {
                throw BusinessException.of("Không có đối tác nào phù hợp với địa điểm đã chọn");
            }

            Map<Integer, List<PartnerShortDTO>> partnersByLocation = partners.stream()
                    .collect(Collectors.groupingBy(partner -> Math.toIntExact(partner.getLocationId())));

            double totalSpending = 0;

            List<PlanDay> planDays = new ArrayList<>();

            for(PlanDayDTO planDayDTO : dto.getDays()) {
                int locationId = planDayDTO.getLocationId();
                String locationName = planDayDTO.getLocationName();
                int dayNumber = planDayDTO.getDayNumber();

                List<PartnerShortDTO> relatedPartners = partnersByLocation.get(locationId);


                String prompt = generatePrompt(totalDays, preferences, totalSpending, locationName, dayNumber, relatedPartners);

                String response = geminiApiService.getGeminiResponse(prompt);

                if (response == null || response.isEmpty()) {
                    throw BusinessException.of("Không thể tạo kế hoạch cho ngày " + dayNumber + " tại " + locationName);
                }

                ObjectMapper mapper = new ObjectMapper();
                PlanDay planDay = mapper.readValue(response, PlanDay.class);

                planDay.getActivities()
                        .parallelStream()
                        .forEach(activityImageEnricher::enrichActivityWithImage);

                double dailySpending = aiPlanParserHelper.extractTotalSpend(response);
                totalSpending += dailySpending;
                planDays.add(planDay);
            }

            String planDetailsPrompt = generatePrompt(locationNames, preferences, totalDays);
            String planDetailsResponse = geminiApiService.getGeminiResponse(planDetailsPrompt);

            ObjectMapper mapper = new ObjectMapper();
            PlanDetail planDetails = mapper.readValue(planDetailsResponse, PlanDetail.class);

            Plan plan = Plan.builder()
                    .title(planDetails.getTitle())
                    .description(planDetails.getDescription())
                    .creatorId(dto.getUserId())
                    .numberDays(totalDays)
                    .totalSpend(totalSpending)
                    .preferences(dto.getPreferences())
                    .days(planDays)
                    .planType(dto.getPlanType())
                    .createdAt(LocalDateTime.now())
                    .planStatus(PlanStatus.CREATED)
                    .build();

            return planRepository.save(plan);

        } catch (Exception ex) {
            throw BusinessException.of("Không thể lấy địa điểm cho kế hoạch", ex);
        }
    }

    private String generatePrompt(List<String> locationNames, String preferences, int totalDays) {

        return String.format("""
                            Bạn là một trợ lý AI chuyên xây dựng chương trình du lịch cá nhân hóa.
            
                            Người dùng đang trong hành trình kéo dài tổng cộng %d ngày.
                            Họ sẽ ghé thăm các địa điểm: %s.
                            Sở thích chính của người dùng bao gồm: %s..
            
                            Hãy tạo nội dung du lịch chi tiết cho mỗi ngày, bao gồm:
                            1. **title**: tiêu đề dễ hiểu, ngắn gọn, thể hiện nội dung chính chuyến đi.
                            2. **description**: mô tả toàn cảnh hoạt động của ngày, ít nhất 250 từ, đầy đủ cảm xúc, mô tả cụ thể các hoạt động sẽ diễn ra từ sáng đến tối theo trình tự thời gian hợp lý.

                            **Kết quả trả về là một JSON object theo định dạng sau**:
                        
                            {
                                title: "",
                                description: ""
                            }
                        """, totalDays, String.join(", ", locationNames), preferences);
    }

    private String generatePrompt(int totalDays, String preferences, double totalSpending, String locationName, int dayNumber, List<PartnerShortDTO> relatedPartners) {
        String partnerContext = buildPartnerContext(locationName, relatedPartners);
        return String.format("""
                                Bạn là một trợ lý AI chuyên xây dựng chương trình du lịch cá nhân hóa.
                        
                                Người dùng đang trong hành trình kéo dài tổng cộng %d ngày.
                                Hôm nay là **ngày thứ %d**, họ sẽ ở tại **%s**.
                                Sở thích chính của người dùng bao gồm: %s.
                                Ngân sách cho ngày hôm nay là khoảng %.0f VNĐ.
                        
                                %s
                            
                                Hãy tạo nội dung du lịch chi tiết cho ngày hôm nay, bao gồm:
                        
                                1. **longDescription**: mô tả toàn cảnh hoạt động của ngày, ít nhất 250 từ, đầy đủ cảm xúc, mô tả cụ thể các hoạt động sẽ diễn ra từ sáng đến tối theo trình tự thời gian hợp lý.
                                2. **activities**: từ 1–3 hoạt động phù hợp với sở thích (mỗi hoạt động gồm: title, content ≥ 50 từ, category, estimatedCost, duration, startTime, endTime)
                                3. **restaurants**: 1–2 nhà hàng địa phương phù hợp với khẩu vị, mỗi nơi có tên, địa chỉ, menu gợi ý, estimatedCost và useDate
                                4. **hotels**: chọn 1 khách sạn phù hợp ngân sách, cung cấp tên, địa chỉ, roomDetails, checkInDate, checkOutDate, estimatedCost, total
                                5. **totalSpend**: tổng chi phí dự kiến cho cả ngày, không vượt quá ngân sách %.0f VNĐ.
                        
                                **Không thêm bất kỳ thông tin nào ngoài định dạng JSON kết quả.**
                                Chỉ được chọn nội dung phù hợp với sở thích. Không tự nghĩ ra hoạt động không liên quan.
                        
                                **Kết quả trả về là một JSON object theo định dạng sau**:
                                %s
                            """, totalDays, dayNumber, locationName, preferences, totalSpending, partnerContext, totalSpending, Constants.AI.PROMPT_DAY_END);
    }

    private String buildPartnerContext(String locationName, List<PartnerShortDTO> partners) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Dưới đây là danh sách các đối tác cung cấp dịch vụ tại %s. Chỉ được sử dụng các đối tác này khi tạo kế hoạch:\n\n", locationName));

        Map<String, List<PartnerShortDTO>> grouped = partners.stream()
                .collect(Collectors.groupingBy(PartnerShortDTO::getPartnerType));

        grouped.forEach((type, list) -> {
            sb.append(String.format("**%s:**\n", type));
            for (PartnerShortDTO p : list) {
                sb.append(String.format("- %s | %s | %s\n",
                        p.getName(),
                        Optional.ofNullable(p.getContactPhone()).orElse("Không có SĐT"),
                        Optional.ofNullable(p.getContactEmail()).orElse("Không có email")));
            }
            sb.append("\n");
        });

        return sb.toString();
    }
}
