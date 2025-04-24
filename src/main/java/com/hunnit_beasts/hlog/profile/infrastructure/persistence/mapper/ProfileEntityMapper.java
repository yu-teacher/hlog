package com.hunnit_beasts.hlog.profile.infrastructure.persistence.mapper;

import com.hunnit_beasts.hlog.profile.domain.model.entity.Profile;
import com.hunnit_beasts.hlog.profile.domain.model.entity.Project;
import com.hunnit_beasts.hlog.profile.domain.model.vo.*;
import com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity.ProfileJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProfileEntityMapper {

    private final ProjectEntityMapper projectMapper;
    private final ExternalLinkEntityMapper externalLinkMapper;

    public ProfileJpaEntity toJpaEntity(Profile domain) {
        if (domain == null) {
            return null;
        }

        ProfileJpaEntity entity = ProfileJpaEntity.builder()
                .id(domain.getId().getValue())
                .userId(domain.getUserId())
                .shortBio(domain.getIntroduction().getShortBio())
                .detailedBio(domain.getIntroduction().getDetailedBio())
                .email(domain.getContact().getEmail())
                .location(domain.getContact().getLocation())
                .techSkills(new HashSet<>(domain.getTechSkills()))
                .isPublic(domain.getSettings().isPublic())
                .customUrl(domain.getSettings().getCustomUrl())
                .status(mapToStatusType(domain.getStatus()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();

        // Clear existing collections and add new items
        entity.getProjects().clear();
        domain.getProjects().forEach(project ->
                entity.addProject(projectMapper.toJpaEntity(project, entity)));

        entity.getExternalLinks().clear();
        domain.getExternalLinks().forEach(link ->
                entity.addExternalLink(externalLinkMapper.toJpaEntity(link, entity)));

        return entity;
    }

    public Profile toDomain(ProfileJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        // Convert projects to domain entities
        List<Project> projects = entity.getProjects().stream()
                .map(projectMapper::toDomain)
                .collect(Collectors.toList());

        // Convert external links to domain value objects
        List<ExternalLink> externalLinks = entity.getExternalLinks().stream()
                .map(externalLinkMapper::toDomain)
                .collect(Collectors.toList());

        // Create value objects
        ProfileId profileId = ProfileId.of(entity.getId());
        Introduction introduction = Introduction.create(
                entity.getShortBio(),
                entity.getDetailedBio()
        );
        Contact contact = Contact.create(
                entity.getEmail(),
                entity.getLocation()
        );
        ProfileSettings settings = ProfileSettings.create(
                entity.isPublic(),
                entity.getCustomUrl()
        );

        // Reconstitute the domain entity
        return Profile.reconstitute(
                profileId,
                entity.getUserId(),
                introduction,
                contact,
                new HashSet<>(entity.getTechSkills()),
                projects,
                externalLinks,
                settings,
                mapToDomainStatus(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ProfileStatus mapToStatusType(ProfileStatus status) {
        return switch (status) {
            case ACTIVE -> ProfileStatus.ACTIVE;
            case INACTIVE -> ProfileStatus.INACTIVE;
            case DELETED -> ProfileStatus.DELETED;
            default -> throw new IllegalArgumentException("Unknown profile status: " + status);
        };
    }

    private ProfileStatus mapToDomainStatus(ProfileStatus statusType) {
        return switch (statusType) {
            case ACTIVE -> ProfileStatus.ACTIVE;
            case INACTIVE -> ProfileStatus.INACTIVE;
            case DELETED -> ProfileStatus.DELETED;
            default -> throw new IllegalArgumentException("Unknown profile status type: " + statusType);
        };
    }
}