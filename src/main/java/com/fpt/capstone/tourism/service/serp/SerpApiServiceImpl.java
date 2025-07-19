package com.fpt.capstone.tourism.service.serp;

import com.fpt.capstone.tourism.service.SerpApiService;
import okhttp3.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SerpApiServiceImpl implements SerpApiService {

    @Value("${serpapi.api.key}")
    private static String API_KEY;
    private static final String API_URL = "https://serpapi.com/search.json";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // Cache theo từ khoá
    private final Map<String, String> imageCache = new ConcurrentHashMap<>();

    @Override
    public String searchFirstImage(String keyword) {
        // Nếu đã có trong cache thì trả ngay
        if (imageCache.containsKey(keyword)) {
            return imageCache.get(keyword);
        }

        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(API_URL)).newBuilder()
                .addQueryParameter("engine", "google")
                .addQueryParameter("q", keyword)
                .addQueryParameter("tbm", "isch") // image search
                .addQueryParameter("api_key", API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected response: " + response);

            assert response.body() != null;
            String responseBody = response.body().string();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode images = root.get("images_results");

            if (images != null && images.isArray() && !images.isEmpty()) {
                String imageUrl = images.get(0).get("original").asText();
                imageCache.put(keyword, imageUrl);
                return imageUrl;
            }
        } catch (Exception e) {
            System.err.println("Lỗi gọi SerpAPI với từ khoá '" + keyword + "': " + e.getMessage());
        }

        // fallback nếu không tìm thấy
        return "https://example.com/default-image.jpg";
    }
}
