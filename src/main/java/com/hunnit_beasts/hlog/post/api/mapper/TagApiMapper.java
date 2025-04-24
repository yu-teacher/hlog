package com.hunnit_beasts.hlog.post.api.mapper;

import com.hunnit_beasts.hlog.post.api.dto.TagResponse;
import com.hunnit_beasts.hlog.post.application.dto.TagDto;
import org.springframework.stereotype.Component;

@Component
public class TagApiMapper {

    public TagResponse toTagResponse(TagDto dto) {
        return TagResponse.builder()
                .name(dto.getName())
                .usageCount(dto.getUsageCount())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}