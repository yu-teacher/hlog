package com.hunnit_beasts.hlog.comment.domain.event;

import com.hunnit_beasts.hlog.comment.domain.model.vo.CommentId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class CommentEvent {
    private final CommentId commentId;
    private final UUID targetId;
    private final UUID authorId;
    private final LocalDateTime occuredAt;

    protected CommentEvent(CommentId commentId, UUID targetId, UUID authorId) {
        this.commentId = commentId;
        this.targetId = targetId;
        this.authorId = authorId;
        this.occuredAt = LocalDateTime.now();
    }
}