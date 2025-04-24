package com.hunnit_beasts.hlog.comment.domain.repository;

import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {
    Comment save(Comment comment);
    Optional<Comment> findById(CommentId id);
    List<Comment> findByTargetId(UUID targetId);
    List<Comment> findByParentId(CommentId parentId);
    List<Comment> findByAuthorId(UUID authorId);
    List<Comment> findActiveCommentByTargetId(UUID targetId);
    List<Comment> findActiveByParentId(CommentId parentId);
    List<Comment> findActiveByAuthorId(UUID authorId);
    void delete(CommentId id);
}