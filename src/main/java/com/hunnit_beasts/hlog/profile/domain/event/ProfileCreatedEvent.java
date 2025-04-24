package com.hunnit_beasts.hlog.profile.domain.event;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ProfileCreatedEvent extends ProfileEvent {
    private final UUID userId;

    public ProfileCreatedEvent(ProfileId profileId, UUID userId) {
        super(profileId);
        this.userId = userId;
    }
}