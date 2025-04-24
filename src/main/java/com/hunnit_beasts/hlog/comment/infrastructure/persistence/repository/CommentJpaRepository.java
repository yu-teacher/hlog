package com.hunnit_beasts.hlog.comment.infrastructure.persistence.repository;

import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentStatus;
import com.hunnit_beasts.hlog.comment.infrastructure.persistence.entity.CommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentJpaEntity, UUID> {
    List<CommentJpaEntity> findByTargetId(UUID targetId);
    List<CommentJpaEntity> findByParentId(UUID parentId);
    List<CommentJpaEntity> findByAuthorId(UUID authorId);

    // 활성 상태의 댓글만 조회하는 메서드들
    List<CommentJpaEntity> findByTargetIdAndStatus(UUID targetId, CommentStatus status);
    List<CommentJpaEntity> findByParentIdAndStatus(UUID parentId, CommentStatus status);
    List<CommentJpaEntity> findByAuthorIdAndStatus(UUID authorId, CommentStatus status);
}