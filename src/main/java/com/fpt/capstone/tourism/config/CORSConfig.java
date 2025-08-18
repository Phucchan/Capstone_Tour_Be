package com.fpt.capstone.tourism.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // Use this instead of allowedOrigins("*")    
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH" ) //  HTTP methods allowed to request
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
