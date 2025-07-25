//package com.fpt.capstone.tourism.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//
//@Configuration
//public class AwsS3Config {
//
//    @Value("${aws.s3.access-key-id}")
//    private String accessKeyId;
//
//    @Value("${aws.s3.secret-access-key}")
//    private String secretAccessKey;
//
//    @Value("${aws.s3.region}")
//    private String region;
//
//    @Bean
//    public S3Client s3Client() {
//        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
//        return S3Client.builder()
//                .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                .region(Region.of(region))
//                .build();
//    }
//}