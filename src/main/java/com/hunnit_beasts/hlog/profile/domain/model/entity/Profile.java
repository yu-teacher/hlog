package com.hunnit_beasts.hlog.profile.domain.model.entity;

import com.hunnit_beasts.hlog.profile.domain.model.vo.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Profile {
    private final ProfileId id;
    private final UUID userId;
    private Introduction introduction;
    private Contact contact;
    private final Set<String> techSkills;
    private final List<Project> projects;
    private final List<ExternalLink> externalLinks;
    private ProfileSettings settings;
    private ProfileStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Profile(ProfileId id, UUID userId, Introduction introduction, Contact contact,
                    Set<String> techSkills, ProfileSettings settings) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.introduction = Objects.requireNonNull(introduction);
        this.contact = Objects.requireNonNull(contact);
        this.techSkills = new HashSet<>(techSkills != null ? techSkills : new HashSet<>());
        this.projects = new ArrayList<>();
        this.externalLinks = new ArrayList<>();
        this.settings = Objects.requireNonNull(settings);
        this.status = ProfileStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Profile create(UUID userId, Introduction introduction, Contact contact,
                                 Set<String> techSkills, ProfileSettings settings) {
        return new Profile(
                ProfileId.create(),
                userId,
                introduction,
                contact,
                techSkills,
                settings
        );
    }

    public static Profile reconstitute(
            ProfileId id, UUID userId, Introduction introduction, Contact contact,
            Set<String> techSkills, List<Project> projects, List<ExternalLink> externalLinks,
            ProfileSettings settings, ProfileStatus status,
            LocalDateTime createdAt, LocalDateTime updatedAt) {

        Profile profile = new Profile(id, userId, introduction, contact, techSkills, settings);
        profile.projects.addAll(projects != null ? projects : Collections.emptyList());
        profile.externalLinks.addAll(externalLinks != null ? externalLinks : Collections.emptyList());
        profile.status = status;
        profile.updatedAt = updatedAt;

        return profile;
    }

    public void updateIntroduction(Introduction introduction) {
        this.introduction = Objects.requireNonNull(introduction);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContact(Contact contact) {
        this.contact = Objects.requireNonNull(contact);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateSettings(ProfileSettings settings) {
        this.settings = Objects.requireNonNull(settings);
        this.updatedAt = LocalDateTime.now();
    }

    public void addTechSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            this.techSkills.add(skill.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeTechSkill(String skill) {
        if (skill != null) {
            this.techSkills.remove(skill.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addProject(Project project) {
        if (project != null) {
            this.projects.add(project);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeProject(ProjectId projectId) {
        if (projectId != null) {
            this.projects.removeIf(p -> p.getId().equals(projectId));
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addExternalLink(ExternalLink link) {
        if (link != null) {
            this.externalLinks.add(link);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeExternalLink(String platform) {
        if (platform != null) {
            this.externalLinks.removeIf(link -> link.getPlatform().equals(platform));
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void deactivate() {
        if (this.status != ProfileStatus.INACTIVE) {
            this.status = ProfileStatus.INACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void activate() {
        if (this.status != ProfileStatus.ACTIVE) {
            this.status = ProfileStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void delete() {
        if (this.status != ProfileStatus.DELETED) {
            this.status = ProfileStatus.DELETED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isDeleted() {
        return this.status == ProfileStatus.DELETED;
    }

    public Set<String> getTechSkills() {
        return Collections.unmodifiableSet(techSkills);
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    public List<ExternalLink> getExternalLinks() {
        return Collections.unmodifiableList(externalLinks);
    }
}