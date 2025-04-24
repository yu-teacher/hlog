package com.hunnit_beasts.hlog.comment.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class UpdateCommentDto {
    private final UUID id;
    private final String content;
}