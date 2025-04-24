package com.hunnit_beasts.hlog.comment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class CreateCommentDto {
    private final String content;
    private final UUID targetId;
    private final UUID authorId;
    private final UUID parentId; // null이면 최상위 댓글
}