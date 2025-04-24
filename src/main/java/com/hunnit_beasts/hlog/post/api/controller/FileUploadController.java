package com.hunnit_beasts.hlog.post.api.controller;

import com.hunnit_beasts.hlog.post.api.dto.FileUploadResponse;
import com.hunnit_beasts.hlog.post.domain.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/uploads")
@RequiredArgsConstructor
public class FileUploadController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/images")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("authorId") UUID authorId) {

        try {
            String imagePath = imageStorageService.storeImage(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    authorId
            );

            String imageUrl = imageStorageService.getImageUrl(imagePath);

            FileUploadResponse response = FileUploadResponse.builder()
                    .url(imageUrl)
                    .filename(file.getOriginalFilename())
                    .size(file.getSize())
                    .build();

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image file", e);
        }
    }

    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImage(@RequestParam("url") String imageUrl) {
        imageStorageService.deleteImage(imageUrl);
        return ResponseEntity.noContent().build();
    }
}