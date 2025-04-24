package com.hunnit_beasts.hlog.comment.api.facade;

import com.hunnit_beasts.hlog.comment.application.dto.CommentDto;

import java.util.List;
import java.util.UUID;

public interface CommentServiceFacade {
    List<CommentDto> getCommentsByTargetId(UUID targetId);
    int getCommentsCountByTargetId(UUID targetId);
    void deleteCommentsByTargetId(UUID targetId);
}