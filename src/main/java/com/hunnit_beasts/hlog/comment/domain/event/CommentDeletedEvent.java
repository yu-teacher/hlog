package com.hunnit_beasts.hlog.comment.domain.event;

import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;

import java.util.UUID;

public class CommentDeletedEvent extends CommentEvent {
    public CommentDeletedEvent(CommentId commentId, UUID targetId, UUID authorId) {
        super(commentId, targetId, authorId);
    }
}