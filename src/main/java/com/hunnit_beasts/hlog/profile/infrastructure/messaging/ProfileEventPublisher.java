package com.hunnit_beasts.hlog.profile.infrastructure.messaging;

import com.hunnit_beasts.hlog.profile.domain.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(ProfileEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishProfileCreated(ProfileCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishProfileUpdated(ProfileUpdatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishProfileDeleted(ProfileDeletedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishProjectAdded(ProjectAddedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishProjectUpdated(ProjectUpdatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishProjectRemoved(ProjectRemovedEvent event) {
        eventPublisher.publishEvent(event);
    }
}