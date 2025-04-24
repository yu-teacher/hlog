package com.hunnit_beasts.hlog.post.api.mapper;

import com.hunnit_beasts.hlog.post.api.dto.CreateSeriesRequest;
import com.hunnit_beasts.hlog.post.api.dto.PostInSeriesResponse;
import com.hunnit_beasts.hlog.post.api.dto.SeriesResponse;
import com.hunnit_beasts.hlog.post.api.dto.UpdateSeriesRequest;
import com.hunnit_beasts.hlog.post.application.dto.CreateSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.PostInSeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.SeriesDto;
import com.hunnit_beasts.hlog.post.application.dto.UpdateSeriesDto;
import com.hunnit_beasts.hlog.user.api.facade.UserServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SeriesApiMapper {

    private final UserServiceFacade userServiceFacade;

    public CreateSeriesDto toCreateSeriesDto(CreateSeriesRequest request) {
        return CreateSeriesDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .authorId(request.getAuthorId())
                .build();
    }

    public UpdateSeriesDto toUpdateSeriesDto(UpdateSeriesRequest request) {
        return UpdateSeriesDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    public SeriesResponse toSeriesResponse(SeriesDto dto) {
        String authorName = dto.getAuthorId() != null
                ? userServiceFacade.getUsernameById(dto.getAuthorId().toString())
                : "Unknown";

        return SeriesResponse.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .authorId(dto.getAuthorId())
                .authorName(authorName)
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .posts(dto.getPosts().stream()
                        .map(this::toPostInSeriesResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private PostInSeriesResponse toPostInSeriesResponse(PostInSeriesDto dto) {
        return PostInSeriesResponse.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .order(dto.getOrder())
                .build();
    }
}