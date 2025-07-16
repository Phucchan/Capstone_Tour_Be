//package com.fpt.capstone.tourism.service.impl;
//
//import com.fpt.capstone.tourism.service.S3Service;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class S3ServiceImpl implements S3Service {
//
//    private final S3Client s3Client;
//
//    @Value("${aws.s3.bucket-name}")
//    private String bucketName;
//
//    @Override
//    public String uploadFile(MultipartFile file, String folder) {
//        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(fileName)
//                .contentType(file.getContentType())
//                .build();
//        try {
//            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload file to S3", e);
//        }
//        return fileName;
//    }
//
//    @Override
//    public void deleteFile(String key) {
//        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//        s3Client.deleteObject(deleteObjectRequest);
//    }
//}