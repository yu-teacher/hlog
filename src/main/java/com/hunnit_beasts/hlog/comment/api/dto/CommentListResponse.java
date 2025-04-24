package com.hunnit_beasts.hlog.comment.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CommentListResponse {
    private final List<CommentResponse> comments;
    private final long totalCount;
}