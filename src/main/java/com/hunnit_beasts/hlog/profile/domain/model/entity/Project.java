package com.hunnit_beasts.hlog.profile.domain.model.entity;

import com.hunnit_beasts.hlog.profile.domain.model.vo.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Project {
    private final ProjectId id;
    private ProjectInfo info;
    private final Set<String> techStack;
    private ProjectLinks links;
    private ProjectStatus status;
    private int displayOrder;
    private boolean isHighlighted;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Project(ProjectId id, ProjectInfo info, Set<String> techStack,
                    ProjectLinks links, int displayOrder) {
        this.id = Objects.requireNonNull(id);
        this.info = Objects.requireNonNull(info);
        this.techStack = new HashSet<>(techStack != null ? techStack : new HashSet<>());
        this.links = Objects.requireNonNull(links);
        this.status = ProjectStatus.IN_PROGRESS;
        this.displayOrder = displayOrder;
        this.isHighlighted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Project create(ProjectInfo info, Set<String> techStack,
                                 ProjectLinks links, int displayOrder) {
        return new Project(
                ProjectId.create(),
                info,
                techStack,
                links,
                displayOrder
        );
    }

    public static Project reconstitute(
            ProjectId id, ProjectInfo info, Set<String> techStack, ProjectLinks links,
            ProjectStatus status, int displayOrder, boolean isHighlighted,
            LocalDateTime createdAt, LocalDateTime updatedAt) {

        Project project = new Project(id, info, techStack, links, displayOrder);
        project.status = status;
        project.isHighlighted = isHighlighted;
        project.updatedAt = updatedAt;

        return project;
    }

    public void updateInfo(ProjectInfo info) {
        this.info = Objects.requireNonNull(info);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLinks(ProjectLinks links) {
        this.links = Objects.requireNonNull(links);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(ProjectStatus status) {
        this.status = Objects.requireNonNull(status);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
        this.updatedAt = LocalDateTime.now();
    }

    public void highlight() {
        this.isHighlighted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void unhighlight() {
        this.isHighlighted = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void addTech(String tech) {
        if (tech != null && !tech.trim().isEmpty()) {
            this.techStack.add(tech.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeTech(String tech) {
        if (tech != null) {
            this.techStack.remove(tech.toLowerCase().trim());
            this.updatedAt = LocalDateTime.now();
        }
    }

    public Set<String> getTechStack() {
        return Collections.unmodifiableSet(techStack);
    }
}