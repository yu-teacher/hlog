package com.hunnit_beasts.hlog.comment.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentDepth;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentStatus;
import com.hunnit_beasts.hlog.comment.infrastructure.persistence.entity.CommentJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CommentEntityMapper {

    public CommentJpaEntity toJpaEntity(Comment comment) {
        return CommentJpaEntity.builder()
                .id(comment.getId().getValue())
                .content(comment.getContent().getValue())
                .targetId(comment.getTargetId())
                .authorId(comment.getAuthorId())
                .parentId(comment.getParentId() != null ? comment.getParentId().getValue() : null)
                .depth(comment.getDepth().getValue())
                .status(mapToJpaStatus(comment.getStatus()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public Comment toDomainEntity(CommentJpaEntity jpaEntity) {
        return Comment.reconstitute(
                CommentId.of(jpaEntity.getId()),
                CommentContent.of(jpaEntity.getContent()),
                jpaEntity.getTargetId(),
                jpaEntity.getAuthorId(),
                jpaEntity.getParentId() != null ? CommentId.of(jpaEntity.getParentId()) : null,
                CommentDepth.of(jpaEntity.getDepth()),
                mapToDomainStatus(jpaEntity.getStatus()),
                jpaEntity.getCreatedAt(),
                jpaEntity.getUpdatedAt()
        );
    }

    private CommentStatus mapToJpaStatus(CommentStatus status) {
        if (status == CommentStatus.ACTIVE) {
            return CommentStatus.ACTIVE;
        } else {
            return CommentStatus.DELETED;
        }
    }

    private CommentStatus mapToDomainStatus(CommentStatus status) {
        if (status == CommentStatus.ACTIVE) {
            return CommentStatus.ACTIVE;
        } else {
            return CommentStatus.DELETED;
        }
    }
}