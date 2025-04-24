package com.hunnit_beasts.hlog.comment.domain.model.entity;

import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentDepth;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Comment {
    private final CommentId id;
    private CommentContent content;
    private final UUID targetId;  // 포스트 UUID
    private final UUID authorId;  // 작성자 UUID
    private final CommentId parentId;  // 부모 댓글 ID (null이면 최상위 댓글)
    private final CommentDepth depth;  // 댓글 깊이
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommentStatus status;

    private Comment(CommentId id, CommentContent content, UUID targetId, UUID authorId,
                    CommentId parentId, CommentDepth depth, CommentStatus status, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.content = Objects.requireNonNull(content);
        this.targetId = Objects.requireNonNull(targetId);
        this.authorId = Objects.requireNonNull(authorId);
        this.parentId = parentId;  // 최상위 댓글인 경우 null 허용
        this.depth = Objects.requireNonNull(depth);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = createdAt;
    }

    // 최상위 댓글 생성
    public static Comment create(CommentContent content, UUID postId, UUID authorId) {
        return new Comment(
                CommentId.create(),
                content,
                postId,
                authorId,
                null,
                CommentDepth.of(0),  // 최상위 댓글은 깊이가 0
                CommentStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    // 대댓글 생성
    public static Comment createReply(CommentContent content, UUID postId, UUID authorId,
                                      CommentId parentId, CommentDepth parentDepth) {
        // 부모 댓글의 깊이 + 1로 설정
        CommentDepth newDepth = parentDepth.increment();

        return new Comment(
                CommentId.create(),
                content,
                postId,
                authorId,
                parentId,
                newDepth,
                CommentStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    // 엔티티 복원 (데이터베이스에서 로드 시 사용)
    public static Comment reconstitute(
            CommentId id, CommentContent content, UUID targetId, UUID authorId,
            CommentId parentId, CommentDepth depth, CommentStatus status,
            LocalDateTime createdAt, LocalDateTime updatedAt) {

        Comment comment = new Comment(id, content, targetId, authorId, parentId, depth, status, createdAt);
        comment.updatedAt = updatedAt;

        return comment;
    }

    // 댓글 내용 수정
    public void updateContent(CommentContent content) {
        this.content = Objects.requireNonNull(content);
        this.updatedAt = LocalDateTime.now();
    }

    // 댓글 삭제 (실제로 삭제하지 않고 상태만 변경)
    public void delete() {
        if (this.status != CommentStatus.DELETED) {
            this.status = CommentStatus.DELETED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // 댓글이 활성 상태인지 확인
    public boolean isActive() {
        return this.status == CommentStatus.ACTIVE;
    }

    // 댓글이 삭제되었는지 확인
    public boolean isDeleted() {
        return this.status == CommentStatus.DELETED;
    }

    // 대댓글인지 확인
    public boolean isReply() {
        return this.parentId != null;
    }
}