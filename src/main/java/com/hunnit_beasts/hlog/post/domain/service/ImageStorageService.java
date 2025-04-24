package com.hunnit_beasts.hlog.post.domain.service;

import java.io.InputStream;
import java.util.UUID;

public interface ImageStorageService {
    String storeImage(InputStream imageStream, String originalFileName, UUID authorId);
    void deleteImage(String imageUrl);
    String getImageUrl(String imagePath);
}