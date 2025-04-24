package com.hunnit_beasts.hlog.profile.domain.event;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileId;
import com.hunnit_beasts.hlog.profile.domain.model.vo.ProjectId;
import lombok.Getter;

@Getter
public class ProjectRemovedEvent extends ProfileEvent {
    private final ProjectId projectId;

    public ProjectRemovedEvent(ProfileId profileId, ProjectId projectId) {
        super(profileId);
        this.projectId = projectId;
    }
}