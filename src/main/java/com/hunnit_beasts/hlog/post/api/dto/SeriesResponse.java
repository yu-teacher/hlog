package com.hunnit_beasts.hlog.post.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeriesResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID authorId;
    private String authorName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<PostInSeriesResponse> posts = new ArrayList<>();
}