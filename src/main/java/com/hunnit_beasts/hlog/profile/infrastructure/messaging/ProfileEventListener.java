package com.hunnit_beasts.hlog.profile.infrastructure.messaging;

import com.hunnit_beasts.hlog.profile.domain.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProfileEventListener {

    @Async
    @EventListener
    public void handleProfileCreatedEvent(ProfileCreatedEvent event) {
        log.info("Profile created: {}, User ID: {}",
                event.getProfileId().getValue(),
                event.getUserId());
        // Additional handling logic if needed
    }

    @Async
    @EventListener
    public void handleProfileUpdatedEvent(ProfileUpdatedEvent event) {
        log.info("Profile updated: {}", event.getProfileId().getValue());
        // Additional handling logic if needed
    }

    @Async
    @EventListener
    public void handleProfileDeletedEvent(ProfileDeletedEvent event) {
        log.info("Profile deleted: {}", event.getProfileId().getValue());
        // Additional handling logic if needed
    }

    @Async
    @EventListener
    public void handleProjectAddedEvent(ProjectAddedEvent event) {
        log.info("Project added to profile {}: {}",
                event.getProfileId().getValue(),
                event.getProjectId().getValue());
        // Additional handling logic if needed
    }

    @Async
    @EventListener
    public void handleProjectUpdatedEvent(ProjectUpdatedEvent event) {
        log.info("Project updated in profile {}: {}",
                event.getProfileId().getValue(),
                event.getProjectId().getValue());
        // Additional handling logic if needed
    }

    @Async
    @EventListener
    public void handleProjectRemovedEvent(ProjectRemovedEvent event) {
        log.info("Project removed from profile {}: {}",
                event.getProfileId().getValue(),
                event.getProjectId().getValue());
        // Additional handling logic if needed
    }
}