package com.hunnit_beasts.hlog.comment.api.facade;

import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;
import com.hunnit_beasts.hlog.comment.application.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentServiceFacadeImpl implements CommentServiceFacade {

    private final CommentService commentService;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByTargetId(UUID targetId) {
        return commentService.getCommentsByTarget(targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCommentsCountByTargetId(UUID targetId) {
        return commentService.getCommentsByTarget(targetId).size();
    }

    @Override
    @Transactional
    public void deleteCommentsByTargetId(UUID targetId) {
        List<CommentDto> comments = commentService.getCommentsByTarget(targetId);
        comments.forEach(comment -> commentService.deleteComment(comment.getId()));
    }
}
