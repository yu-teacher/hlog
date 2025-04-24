package com.hunnit_beasts.hlog.comment.domain.event;

import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;

import java.util.UUID;

public class CommentUpdatedEvent extends CommentEvent {
    public CommentUpdatedEvent(CommentId commentId, UUID targetId, UUID authorId) {
        super(commentId, targetId, authorId);
    }
}