package com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity;

import com.hunnit_beasts.hlog.profile.domain.model.vo.ProfileStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfileJpaEntity {

    @Id
    @Column(name = "profile_id")
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "short_bio", length = 100)
    private String shortBio;

    @Column(name = "detailed_bio", columnDefinition = "TEXT")
    private String detailedBio;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "location", length = 100)
    private String location;

    @ElementCollection
    @CollectionTable(name = "profile_tech_skills",
            joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    @Builder.Default
    private Set<String> techSkills = new HashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectJpaEntity> projects = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExternalLinkJpaEntity> externalLinks = new ArrayList<>();

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "custom_url", unique = true)
    private String customUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProfileStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public void addProject(ProjectJpaEntity project) {
        projects.add(project);
        project.setProfile(this);
    }

    public void removeProject(ProjectJpaEntity project) {
        projects.remove(project);
        project.setProfile(null);
    }

    public void addExternalLink(ExternalLinkJpaEntity link) {
        externalLinks.add(link);
        link.setProfile(this);
    }

    public void removeExternalLink(ExternalLinkJpaEntity link) {
        externalLinks.remove(link);
        link.setProfile(null);
    }
}