package com.hunnit_beasts.hlog.comment.api.mapper;

import com.hunnit_beasts.hlog.comment.api.dto.CommentResponse;
import com.hunnit_beasts.hlog.comment.api.dto.CreateCommentRequest;
import com.hunnit_beasts.hlog.comment.api.dto.UpdateCommentRequest;
import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.CreateCommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.UpdateCommentDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommentApiMapper {

    public CreateCommentDto toCreateDto(CreateCommentRequest request, UUID authorId) {
        return CreateCommentDto.builder()
                .content(request.getContent())
                .targetId(request.getTargetId())
                .authorId(authorId)
                .parentId(request.getParentId())
                .build();
    }

    public UpdateCommentDto toUpdateDto(UpdateCommentRequest request) {
        return UpdateCommentDto.builder()
                .id(request.getId())
                .content(request.getContent())
                .build();
    }

    public CommentResponse toResponse(CommentDto dto, String authorName) {
        return CommentResponse.builder()
                .id(dto.getId())
                .content(dto.getContent())
                .targetId(dto.getTargetId())
                .authorId(dto.getAuthorId())
                .authorName(authorName)
                .parentId(dto.getParentId())
                .depth(dto.getDepth())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}