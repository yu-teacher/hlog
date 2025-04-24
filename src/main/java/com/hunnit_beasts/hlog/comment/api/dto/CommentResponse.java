package com.hunnit_beasts.hlog.comment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponse {
    private final UUID id;
    private final String content;
    private final UUID targetId;
    private final UUID authorId;
    private final String authorName; // User 도메인에서 가져올 정보
    private final UUID parentId;
    private final int depth;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}