package com.fpt.capstone.tourism.controller.file;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fpt.capstone.tourism.service.S3Service;

@RestController
@RequestMapping("/public/file")
@RequiredArgsConstructor
public class FileUploadController {
        private final S3Service s3Service;

        @Value("${aws.s3.bucket-url}")
        private String bucketUrl;

        @PostMapping("/upload")
        public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
            String key = s3Service.uploadFile(file, "uploads");
            String fileUrl = bucketUrl + "/" + key;
            return ResponseEntity.ok(fileUrl);
        }
    }