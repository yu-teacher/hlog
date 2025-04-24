package com.hunnit_beasts.hlog.profile.domain.event;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;
import com.hunnit_beasts.hlog.profile.domain.model.vo.ProjectId;
import lombok.Getter;

@Getter
public class ProjectAddedEvent extends ProfileEvent {
    private final ProjectId projectId;

    public ProjectAddedEvent(ProfileId profileId, ProjectId projectId) {
        super(profileId);
        this.projectId = projectId;
    }
}