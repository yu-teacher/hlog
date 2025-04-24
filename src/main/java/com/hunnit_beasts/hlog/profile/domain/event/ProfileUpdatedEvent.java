package com.hunnit_beasts.hlog.profile.domain.event;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;

public class ProfileUpdatedEvent extends ProfileEvent {
    public ProfileUpdatedEvent(ProfileId profileId) {
        super(profileId);
    }
}