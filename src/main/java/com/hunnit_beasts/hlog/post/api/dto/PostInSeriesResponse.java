package com.hunnit_beasts.hlog.post.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostInSeriesResponse {
    private UUID id;
    private String title;
    private Integer order;
}