package com.hunnit_beasts.hlog.post.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private UUID id;
    private String title;
    private String content;
    private String contentFormat;
    private String status;
    private UUID authorId;
    private String authorName; // 작성자 이름 표시를 위한 추가 필드

    @Builder.Default
    private Set<String> tagNames = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private String htmlContent; // 마크다운을 HTML로 변환한 결과
}