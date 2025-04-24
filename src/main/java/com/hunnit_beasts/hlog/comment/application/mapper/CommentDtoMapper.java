package com.hunnit_beasts.hlog.comment.application.mapper;

import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.CreateCommentDto;
import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper {

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId().getValue())
                .content(comment.getContent().getValue())
                .targetId(comment.getTargetId())
                .authorId(comment.getAuthorId())
                .parentId(comment.getParentId() != null ? comment.getParentId().getValue() : null)
                .depth(comment.getDepth().getValue())
                .status(comment.getStatus().toString())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public Comment toEntity(CreateCommentDto dto) {
        if (dto.getParentId() == null) {
            // 최상위 댓글 생성
            return Comment.create(
                    CommentContent.of(dto.getContent()),
                    dto.getTargetId(),
                    dto.getAuthorId()
            );
        } else {
            // 대댓글 생성 (부모 댓글의 깊이 정보가 필요함)
            // 이 부분은 서비스 계층에서 부모 댓글 조회 후 처리해야 함
            return null;
        }
    }
}