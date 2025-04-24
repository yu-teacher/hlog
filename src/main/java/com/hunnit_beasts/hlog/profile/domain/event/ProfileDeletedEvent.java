package com.hunnit_beasts.hlog.profile.domain.event;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;

public class ProfileDeletedEvent extends ProfileEvent {
    public ProfileDeletedEvent(ProfileId profileId) {
        super(profileId);
    }
}