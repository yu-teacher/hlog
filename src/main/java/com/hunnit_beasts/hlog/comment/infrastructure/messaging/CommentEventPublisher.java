package com.hunnit_beasts.hlog.comment.infrastructure.messaging;

import com.hunnit_beasts.hlog.comment.domain.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(CommentEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}