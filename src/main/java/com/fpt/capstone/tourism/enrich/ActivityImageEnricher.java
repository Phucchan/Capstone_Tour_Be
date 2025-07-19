package com.fpt.capstone.tourism.enrich;


import com.fpt.capstone.tourism.model.domain.Activity;
import com.fpt.capstone.tourism.service.SerpApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityImageEnricher {

    private final SerpApiService serpApiService;

    public void enrichActivityWithImage(Activity activity) {
        // Tạo từ khoá tìm kiếm từ tiêu đề hoạt động + "du lịch Việt Nam"
        String keyword = activity.getTitle() + " du lịch Việt Nam";

        // Gọi SerpAPI để tìm ảnh
        String imageUrl = serpApiService.searchFirstImage(keyword);

        // Gán vào activity
        activity.setImageUrl(imageUrl);
    }
}