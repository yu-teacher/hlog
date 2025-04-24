package com.hunnit_beasts.hlog.comment.infrastructure.messaging;

import com.hunnit_beasts.hlog.comment.domain.event.CommentCreatedEvent;
import com.hunnit_beasts.hlog.comment.domain.event.CommentDeletedEvent;
import com.hunnit_beasts.hlog.comment.domain.event.CommentUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentEventListener {

    @EventListener
    public void handleCommentCreatedEvent(CommentCreatedEvent event) {
        log.info("Comment created: {}", event.getCommentId().getValue());
        // 여기에 알림 로직 추가 가능
    }

    @EventListener
    public void handleCommentUpdatedEvent(CommentUpdatedEvent event) {
        log.info("Comment updated: {}", event.getCommentId().getValue());
        // 여기에 알림 로직 추가 가능
    }

    @EventListener
    public void handleCommentDeletedEvent(CommentDeletedEvent event) {
        log.info("Comment deleted: {}", event.getCommentId().getValue());
        // 여기에 알림 로직 추가 가능
    }
}