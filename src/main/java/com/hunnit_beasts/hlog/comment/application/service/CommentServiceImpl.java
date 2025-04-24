package com.hunnit_beasts.hlog.comment.application.service;

import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.CreateCommentDto;
import com.hunnit_beasts.hlog.comment.application.dto.UpdateCommentDto;
import com.hunnit_beasts.hlog.comment.application.mapper.CommentDtoMapper;
import com.hunnit_beasts.hlog.comment.domain.event.CommentCreatedEvent;
import com.hunnit_beasts.hlog.comment.domain.event.CommentDeletedEvent;
import com.hunnit_beasts.hlog.comment.domain.event.CommentUpdatedEvent;
import com.hunnit_beasts.hlog.comment.domain.model.entity.Comment;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentContent;
import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import com.hunnit_beasts.hlog.comment.domain.repository.CommentRepository;
import com.hunnit_beasts.hlog.comment.infrastructure.messaging.CommentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentDtoMapper commentDtoMapper;
    private final CommentEventPublisher commentEventPublisher;

    @Override
    @Transactional
    public CommentDto createComment(CreateCommentDto createCommentDto) {
        Comment comment;

        if (createCommentDto.getParentId() == null) {
            // 최상위 댓글 생성
            comment = Comment.create(
                    CommentContent.of(createCommentDto.getContent()),
                    createCommentDto.getTargetId(),
                    createCommentDto.getAuthorId()
            );
        } else {
            // 부모 댓글 조회
            CommentId parentId = CommentId.of(createCommentDto.getParentId());
            Comment parentComment = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

            // 부모 댓글의 깊이를 기반으로 대댓글 생성
            comment = Comment.createReply(
                    CommentContent.of(createCommentDto.getContent()),
                    createCommentDto.getTargetId(),
                    createCommentDto.getAuthorId(),
                    parentId,
                    parentComment.getDepth()
            );
        }

        Comment savedComment = commentRepository.save(comment);

        // 이벤트 발행
        commentEventPublisher.publish(new CommentCreatedEvent(
                savedComment.getId(),
                savedComment.getTargetId(),
                savedComment.getAuthorId()
        ));

        return commentDtoMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentDto updateCommentDto) {
        CommentId commentId = CommentId.of(updateCommentDto.getId());
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.updateContent(CommentContent.of(updateCommentDto.getContent()));
        Comment updatedComment = commentRepository.save(comment);

        // 이벤트 발행
        commentEventPublisher.publish(new CommentUpdatedEvent(
                updatedComment.getId(),
                updatedComment.getTargetId(),
                updatedComment.getAuthorId()
        ));

        return commentDtoMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(UUID commentId) {
        CommentId id = CommentId.of(commentId);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.delete();
        Comment deletedComment = commentRepository.save(comment);

        // 이벤트 발행
        commentEventPublisher.publish(new CommentDeletedEvent(
                deletedComment.getId(),
                deletedComment.getTargetId(),
                deletedComment.getAuthorId()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getComment(UUID commentId) {
        CommentId id = CommentId.of(commentId);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        return commentDtoMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByTarget(UUID targetId) {
        List<Comment> comments = commentRepository.findActiveCommentByTargetId(targetId);
        return comments.stream()
                .map(commentDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByParent(UUID parentId) {
        CommentId id = CommentId.of(parentId);
        List<Comment> comments = commentRepository.findActiveByParentId(id);
        return comments.stream()
                .map(commentDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByAuthor(UUID authorId) {
        List<Comment> comments = commentRepository.findActiveByAuthorId(authorId);
        return comments.stream()
                .map(commentDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}