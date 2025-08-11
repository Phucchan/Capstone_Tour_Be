package com.fpt.capstone.tourism.service.impl.plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fpt.capstone.tourism.constants.Constants;
import com.fpt.capstone.tourism.dto.common.partner.PartnerShortDTO;
import com.fpt.capstone.tourism.dto.general.GeneralResponse;
import com.fpt.capstone.tourism.dto.general.PagingDTO;
import com.fpt.capstone.tourism.dto.request.plan.PlanDayDTO;
import com.fpt.capstone.tourism.dto.request.plan.PlanGenerationRequestDTO;
import com.fpt.capstone.tourism.dto.response.PublicLocationDTO;
import com.fpt.capstone.tourism.enrich.Enricher;
import com.fpt.capstone.tourism.exception.common.BusinessException;
import com.fpt.capstone.tourism.mapper.LocationMapper;
import com.fpt.capstone.tourism.model.Location;
import com.fpt.capstone.tourism.model.domain.PlanDay;
import com.fpt.capstone.tourism.model.domain.PlanDetail;
import com.fpt.capstone.tourism.model.enums.PlanStatus;
import com.fpt.capstone.tourism.model.mongo.Plan;
import com.fpt.capstone.tourism.repository.LocationRepository;
import com.fpt.capstone.tourism.repository.mongo.PlanRepository;
import com.fpt.capstone.tourism.repository.partner.PartnerRepository;
import com.fpt.capstone.tourism.service.GeminiApiService;
import com.fpt.capstone.tourism.service.plan.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private final Enricher enricher;
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

            double budgetMin = dto.getBudget().getMin();
            double budgetMax = dto.getBudget().getMax();

            List<PlanDay> planDays = new ArrayList<>();

            for(PlanDayDTO planDayDTO : dto.getDays()) {
                int locationId = planDayDTO.getLocationId();
                String locationName = planDayDTO.getLocationName();
                int dayNumber = planDayDTO.getDayNumber();

                List<PartnerShortDTO> relatedPartners = partnersByLocation.get(locationId);




                String prompt = generatePrompt(totalDays, preferences, budgetMin, budgetMax, totalSpending, locationName, dayNumber, relatedPartners);

                String response = geminiApiService.getGeminiResponse(prompt);

                if (response == null || response.isEmpty()) {
                    throw BusinessException.of("Không thể tạo kế hoạch cho ngày " + dayNumber + " tại " + locationName);
                }

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                PlanDay planDay = mapper.readValue(cleanJson(response), PlanDay.class);

                planDay.getActivities()
                        .parallelStream()
                        .forEach(enricher::enrichActivityWithImage);

                double dailySpending = planDay.getTotalSpend();
                totalSpending += dailySpending;
                planDays.add(planDay);
            }

            String planDetailsPrompt = generatePrompt(locationNames, preferences, totalDays);
            String planDetailsResponse = geminiApiService.getGeminiResponse(planDetailsPrompt);

            ObjectMapper mapper = new ObjectMapper();
            PlanDetail planDetails = mapper.readValue(cleanJson(planDetailsResponse), PlanDetail.class);

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
                    .planStatus(PlanStatus.PENDING)
                    .transportationType(planDetails.getTransportationType())
                    .build();

            enricher.enrichPlanWithImage(plan, String.join(", ", locationNames));

            return planRepository.save(plan);

        } catch (Exception ex) {
            throw BusinessException.of(ex.getMessage(), ex);
        }
    }

    @Override
    public Plan getPlanById(String planId) {
        try {
            return planRepository.findById(planId)
                    .orElseThrow(() -> BusinessException.of("Không tìm thấy kế hoạch với ID: " + planId));
        } catch (Exception ex) {
            throw BusinessException.of("Lỗi khi lấy kế hoạch", ex);
        }
    }

    private String cleanJson(String json) {
        return json.replaceFirst("^```json\\s*", "")
                .replaceFirst("\\s*```$", "");
    }

    private String generatePrompt(List<String> locationNames, String preferences, int totalDays) {

        return String.format("""
                            Bạn là một trợ lý AI chuyên xây dựng chương trình du lịch cá nhân hóa.
            
                            Người dùng đang trong hành trình kéo dài tổng cộng %d ngày.
                            Họ sẽ ghé thăm các địa điểm: %s.
                            Sở thích chính của người dùng bao gồm: %s..
            
                            Hãy tạo nội dung du lịch chi tiết cho cả kế hoach, bao gồm:
                            1. **title**: tiêu đề dễ hiểu, ngắn gọn, thể hiện nội dung chính chuyến đi.
                            2. **description**: mô tả toàn cảnh hoạt động của chuyến đi, ít nhất 100 từ, đầy đủ cảm xúc, mô tả chung một nội dung của kế hoạch.
                            3. **transportationType**: Một hoặc 2 loại phương tiện di chuyển chính trong chuyến đi ("Flight, "Car").

                            **Kết quả trả về là một JSON object theo định dạng sau**:
                        
                            {
                                title: "",
                                description: "",
                                transportationType: "Flight, Car"
                            }
                        """, totalDays, String.join(", ", locationNames), preferences);
    }

    private String generatePrompt(int totalDays, String preferences, double budgetMin, double budgetMax, double totalSpending, String locationName, int dayNumber, List<PartnerShortDTO> relatedPartners) {
        String partnerContext = buildPartnerContext(locationName, relatedPartners);
        return String.format("""
                                Bạn là một trợ lý AI chuyên xây dựng chương trình du lịch cá nhân hóa.
                        
                                Người dùng đang trong hành trình kéo dài tổng cộng %d ngày.
                                Hôm nay là **ngày thứ %d**, họ sẽ ở tại **%s**.
                                Sở thích chính của người dùng bao gồm: %s.
                                Tổng chi phí mong muốn cho chuyến đi đến nay là khoảng %.0f - %.0f VNĐ.
                                Tổng tiền đã chi tiêu đến nay là %.0f VNĐ.
                            
                            
                                Hãy cố gắng thiết kế lịch trình có chi phí hợp lý và xứng đáng với mức ngân sách đã đề ra.
                                Đồng thời, hãy phân bổ chi tiêu hợp lý cho từng ngày.
                                
                                Cụ thể:
                                - Trong mỗi ngày, hãy ưu tiên xây dựng các hoạt động có chất lượng tốt tương xứng với ngân sách tổng.
                                - Tổng ngân sách sẽ được chia đều tương đối theo số ngày trong hành trình, nhưng có thể linh hoạt điều chỉnh theo từng ngày.
                                - Tổng chi phí của tất cả chuyến đi nên đạt **tối thiểu khoảng 80-90%% ngân sách**, để đảm bảo người dùng tận hưởng trọn vẹn trải nghiệm, đồng thời vẫn giữ lại một phần dự phòng nhỏ cho các trường hợp phát sinh.
                                - Tránh việc chi tiêu quá tiết kiệm hoặc chọn phương án miễn phí nếu không thật sự cần thiết.
                                - Tránh tạo ra lịch trình ngày có chi phí quá thấp hoặc quá cao so với phần còn lại của chuyến đi.
                        
                                %s
                            
                                Hãy tạo nội dung du lịch chi tiết cho ngày hôm nay, bao gồm:
                        
                                1. **longDescription**: mô tả toàn cảnh hoạt động của ngày, ít nhất 50 từ, đầy đủ cảm xúc, mô tả cụ thể các hoạt động sẽ diễn ra từ sáng đến tối theo trình tự thời gian hợp lý.
                                2. **activities**: từ 3-4 hoạt động phù hợp với sở thích (mỗi hoạt động gồm: title, content <=30 từ ngắn gọn và dễ hiểu, category, estimatedCost, duration, startTime, endTime)
                                    - Ưu tiên các hoạt động có trả phí, có giá trị trải nghiệm tương xứng và góp phần nâng tổng chi phí ngày lên mức hợp lý (gần mức ngân sách đã đề ra).
                                    - Chỉ chọn hoạt động miễn phí nếu thật sự nổi bật và phù hợp sở thích. Tránh đưa ra nhiều hoạt động miễn phí khiến tổng chi tiêu bị thấp.
                                3. **restaurants**: 1 nhà hàng địa phương phù hợp với khẩu vị, mỗi nơi có tên, địa chỉ, menu gợi ý, estimatedCost và useDate
                                4. **hotels**: chọn 1 khách sạn phù hợp ngân sách, cung cấp tên, địa chỉ, roomDetails, checkInDate, checkOutDate, estimatedCost, total
                                5. **totalSpend**: tổng chi phí dự kiến cho cả ngày, không vượt quá ngân sách %.0f VNĐ.
                                6. **estimatedCost**: Với mỗi hoạt động, nhà hàng và khách sạn, hãy đánh giá chi phí phù hợp theo mặt bằng Việt Nam và ghi vào trường `estimatedCost` (đơn vị VNĐ). Nếu miễn phí, để giá trị là `0`.
                                    - Nếu một hoạt động là **miễn phí hoàn toàn** (ví dụ: đi bộ quanh hồ, tham quan đền chùa), hãy đặt `estimatedCost: 0`.
                                    - Với các nhà hàng và khách sạn **không được để estimatedCost = 0**.
                                    - Ước tính theo mặt bằng giá thực tế – ví dụ: bữa ăn phổ thông 50.000–100.000 VNĐ, khách sạn bình dân 300.000–600.000 VNĐ/đêm...
                                7. **title**: tiêu đề dễ hiểu, ngắn gọn, thể hiện nội dung chính của ngày hôm nay.
                                
                                %s
                            """, totalDays, dayNumber, locationName, preferences, budgetMin, budgetMax, totalSpending, partnerContext, totalSpending, Constants.AI.PROMPT_DAY_END);
    }

    private String buildPartnerContext(String locationName, List<PartnerShortDTO> partners) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Dưới đây là danh sách các đối tác cung cấp dịch vụ tại %s. Chỉ được sử dụng các đối tác này khi tạo kế hoạch:\n\n", locationName));

        Map<String, List<PartnerShortDTO>> grouped = partners.stream()
                .collect(Collectors.groupingBy(PartnerShortDTO::getPartnerType));

        grouped.forEach((type, list) -> {
            sb.append(String.format("**%s:**\n", type));
            for (PartnerShortDTO p : list) {
                sb.append(String.format("- %s | %s | %s | logoUrl: %s | website: %s\n",
                        p.getName(),
                        Optional.ofNullable(p.getContactPhone()).orElse("Không có SĐT"),
                        Optional.ofNullable(p.getContactEmail()).orElse("Không có email"),
                        Optional.ofNullable(p.getLogoUrl()).orElse("Không có logo"),
                        Optional.ofNullable(p.getWebsiteUrl()).orElse("Không có website"))
                );
            }
            sb.append("\n");
        });

        return sb.toString();
    }


    @Override
    public GeneralResponse<PagingDTO<Plan>>  getPlans(int page, int size, String sortField, String sortDirection, Integer userId) {
        try {
            // chuẩn hoá & fallback
            int safePage = Math.max(page, 0);                // Spring page 0-based
            int safeSize = Math.max(size, 1);
            String safeSortField = StringUtils.hasText(sortField) ? sortField : "createdAt";
            Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(direction, safeSortField));

            // query mongo
            Page<Plan> result = (userId == null)
                    ? planRepository.findAll(pageable)
                    : planRepository.findByCreatorIdAndPlanStatus(userId, PlanStatus.CREATED, pageable);

            // build paging dto
            PagingDTO<Plan> paging = new PagingDTO<>();
            paging.setItems(result.getContent());
            paging.setPage(result.getNumber());
            paging.setSize(result.getSize());
            paging.setTotal(result.getTotalElements());

            return GeneralResponse.of(paging);

        } catch (IllegalArgumentException iae) {
            // ví dụ khi sortField không hợp lệ hoặc sortDirection sai format
            throw BusinessException.of("Tham số sắp xếp không hợp lệ", iae);
        } catch (Exception ex) {
            throw BusinessException.of("Lấy dữ liệu thất bại", ex);
        }
    }

    @Override
    public String savePlan(String id) {
        try {
            planRepository.updatePlanStatusById(id, PlanStatus.CREATED);
            return id;
        } catch (Exception e){
            throw BusinessException.of("Lấy dữ liệu thất bại", e);
        }
    }

}
