package com.hunnit_beasts.hlog.comment.infrastructure.repository;

import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentStatus;
import com.hunnit_beasts.hlog.comment.domain.repository.CommentRepository;
import com.hunnit_beasts.hlog.comment.infrastructure.persistence.entity.CommentJpaEntity;
import com.hunnit_beasts.hlog.comment.infrastructure.persistence.mapper.CommentEntityMapper;
import com.hunnit_beasts.hlog.comment.infrastructure.persistence.repository.CommentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepositoryImpl implements CommentRepository {

    private final CommentJpaRepository commentJpaRepository;
    private final CommentEntityMapper commentEntityMapper;

    @Override
    public Comment save(Comment comment) {
        CommentJpaEntity jpaEntity = commentEntityMapper.toJpaEntity(comment);
        CommentJpaEntity savedEntity = commentJpaRepository.save(jpaEntity);
        return commentEntityMapper.toDomainEntity(savedEntity);
    }

    @Override
    public Optional<Comment> findById(CommentId id) {
        return commentJpaRepository.findById(id.getValue())
                .map(commentEntityMapper::toDomainEntity);
    }

    @Override
    public List<Comment> findByTargetId(UUID targetId) {
        return commentJpaRepository.findByTargetId(targetId).stream()
                .map(commentEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByParentId(CommentId parentId) {
        return commentJpaRepository.findByParentId(parentId.getValue()).stream()
                .map(commentEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByAuthorId(UUID authorId) {
        return commentJpaRepository.findByAuthorId(authorId).stream()
                .map(commentEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findActiveCommentByTargetId(UUID targetId) {
        return commentJpaRepository.findByTargetIdAndStatus(targetId, CommentStatus.ACTIVE)
                .stream()
                .map(commentEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findActiveByParentId(CommentId parentId) {
        return commentJpaRepository.findByParentIdAndStatus(parentId.getValue(), CommentStatus.ACTIVE)
                .stream()
                .map(commentEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findActiveByAuthorId(UUID authorId) {
        return commentJpaRepository.findByAuthorIdAndStatus(authorId, CommentStatus.ACTIVE)
                .stream()
                .map(commentEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(CommentId id) {
        commentJpaRepository.deleteById(id.getValue());
    }
}