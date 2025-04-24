package com.hunnit_beasts.hlog.profile.domain.event;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public abstract class ProfileEvent {
    private final ProfileId profileId;
    private final LocalDateTime occurredOn;

    protected ProfileEvent(ProfileId profileId) {
        this.profileId = Objects.requireNonNull(profileId);
        this.occurredOn = LocalDateTime.now();
    }
}