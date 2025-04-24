package com.hunnit_beasts.hlog.post.infrastructure.service;

import com.hunnit_beasts.hlog.post.domain.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Value("${upload.directory.${os.name}}")
    private String uploadDir;

    @Value("${app.image.url.prefix:/uploads/}")
    private String imageUrlPrefix;

    @Override
    public String storeImage(InputStream imageStream, String originalFileName, UUID authorId) {
        try {
            String fileExtension = StringUtils.getFilenameExtension(originalFileName);
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 날짜 기반 디렉토리 구조 (년/월/일)
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String relativePath = authorId + "/" + datePath;

            // 디렉토리 생성
            Path uploadPath = Paths.get(uploadDir, relativePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path destination = uploadPath.resolve(fileName);
            Files.copy(imageStream, destination, StandardCopyOption.REPLACE_EXISTING);

            // 이미지 경로 반환
            return relativePath + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        try {
            // URL에서 상대 경로 추출
            String relativePath = imageUrl.replace(imageUrlPrefix, "");

            // 파일 삭제
            Path filePath = Paths.get(uploadDir, relativePath);
            Files.deleteIfExists(filePath);

            // 빈 디렉토리 정리 (선택적)
            cleanEmptyDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    @Override
    public String getImageUrl(String imagePath) {
        return imageUrlPrefix + imagePath;
    }

    private void cleanEmptyDirectories(Path directory) throws IOException {
        if (directory == null || !Files.exists(directory)) {
            return;
        }

        // 디렉토리가 비어있는지 확인
        if (Files.list(directory).findAny().isEmpty()) {
            Files.delete(directory);
            cleanEmptyDirectories(directory.getParent());
        }
    }
}