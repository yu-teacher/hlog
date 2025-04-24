package com.hunnit_beasts.hlog.user.infrastructure.messaging;

import com.hunnit_beasts.hlog.user.domain.event.UserEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public UserEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(UserEvent event) {
        eventPublisher.publishEvent(event);
    }
}