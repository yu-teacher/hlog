package com.hunnit_beasts.hlog.comment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CommentDto {
    private final UUID id;
    private final String content;
    private final UUID targetId;
    private final UUID authorId;
    private final UUID parentId; // null이면 최상위 댓글
    private final int depth;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}