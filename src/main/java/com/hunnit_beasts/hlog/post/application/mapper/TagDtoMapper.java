package com.hunnit_beasts.hlog.post.application.mapper;

import com.hunnit_beasts.hlog.post.application.dto.TagDto;
import com.hunnit_beasts.hlog.post.domain.model.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TagDtoMapper {

    public TagDto toDto(Tag tag) {
        return TagDto.builder()
                .name(tag.getName())
                .usageCount(tag.getUsageCount())
                .createdAt(tag.getCreatedAt())
                .build();
    }

    public List<TagDto> toDtoList(List<Tag> tags) {
        return tags.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}