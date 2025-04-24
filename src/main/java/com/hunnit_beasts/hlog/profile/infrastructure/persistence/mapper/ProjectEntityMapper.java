package com.hunnit_beasts.hlog.profile.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.profile.domain.model.entity.Project;
import com.hunnit_beasts.hlog.profile.domain.model.vo.*;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity.ProfileJpaEntity;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity.ProjectJpaEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ProjectEntityMapper {

    public ProjectJpaEntity toJpaEntity(Project domain, ProfileJpaEntity profile) {
        if (domain == null) {
            return null;
        }

        return ProjectJpaEntity.builder()
                .id(domain.getId().getValue())
                .profile(profile)
                .title(domain.getInfo().getTitle())
                .description(domain.getInfo().getDescription())
                .startDate(domain.getInfo().getStartDate())
                .endDate(domain.getInfo().getEndDate())
                .techStack(new HashSet<>(domain.getTechStack()))
                .githubUrl(domain.getLinks().getGithubUrl())
                .demoUrl(domain.getLinks().getDemoUrl())
                .status(mapToStatusType(domain.getStatus()))
                .displayOrder(domain.getDisplayOrder())
                .isHighlighted(domain.isHighlighted())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Project toDomain(ProjectJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        // Create value objects
        ProjectId projectId = ProjectId.of(entity.getId());
        ProjectInfo info = ProjectInfo.create(
                entity.getTitle(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate()
        );
        ProjectLinks links = ProjectLinks.create(
                entity.getGithubUrl(),
                entity.getDemoUrl()
        );

        // Reconstitute the domain entity
        return Project.reconstitute(
                projectId,
                info,
                new HashSet<>(entity.getTechStack()),
                links,
                mapToDomainStatus(entity.getStatus()),
                entity.getDisplayOrder(),
                entity.isHighlighted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ProjectStatus mapToStatusType(ProjectStatus status) {
        return switch (status) {
            case IN_PROGRESS -> ProjectStatus.IN_PROGRESS;
            case COMPLETED -> ProjectStatus.COMPLETED;
            default -> throw new IllegalArgumentException("Unknown project status: " + status);
        };
    }

    private ProjectStatus mapToDomainStatus(ProjectStatus statusType) {
        return switch (statusType) {
            case IN_PROGRESS -> ProjectStatus.IN_PROGRESS;
            case COMPLETED -> ProjectStatus.COMPLETED;
            default -> throw new IllegalArgumentException("Unknown project status type: " + statusType);
        };
    }
}