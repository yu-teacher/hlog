package com.hunnit_beasts.hlog.post.infrastructure.messaging;

import com.hunnit_beasts.hlog.post.domain.event.PostEvent;
import com.hunnit_beasts.hlog.post.domain.event.SeriesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public PostEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(PostEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publish(SeriesEvent event) {
        eventPublisher.publishEvent(event);
    }
}
