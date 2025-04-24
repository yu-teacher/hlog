package com.hunnit_beasts.hlog.post.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSeriesDto {
    private String name;
    private String description;
    private UUID authorId;
}