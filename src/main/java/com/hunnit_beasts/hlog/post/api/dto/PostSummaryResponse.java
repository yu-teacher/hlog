package com.hunnit_beasts.hlog.post.api.dto;

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
public class PostSummaryResponse {
    private UUID id;
    private String title;
    private String summary;
    private String status;
    private UUID authorId;
    private String authorName;

    @Builder.Default
    private Set<String> tagNames = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}