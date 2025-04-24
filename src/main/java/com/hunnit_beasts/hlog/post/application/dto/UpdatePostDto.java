package com.hunnit_beasts.hlog.post.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostDto {
    private String title;
    private String content;
    private String contentFormat;

    @Builder.Default
    private Set<String> tagNames = new HashSet<>();
}