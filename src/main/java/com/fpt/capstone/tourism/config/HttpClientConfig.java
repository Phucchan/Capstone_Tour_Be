package com.fpt.capstone.tourism.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.impl.client.CloseableHttpClient;


@Configuration
public class HttpClientConfig {
    @Value("${endpoints.connect-timeout}")
    private int connectionTimeout;

    @Value("${endpoints.read-timeout}")
    private int readTimeout;

    @Bean
    public CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(readTimeout)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

}
