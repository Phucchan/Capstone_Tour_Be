package com.fpt.capstone.tourism.service.impl;

import com.fpt.capstone.tourism.service.GeminiApiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiApiServiceImpl implements GeminiApiService {

    private final ChatClient client;

    public GeminiApiServiceImpl(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    @Override
    public String getGeminiResponse(String prompt) {
        return client.prompt(prompt)
                .call()
                .content();
    }




}
