package com.hunnit_beasts.hlog.profile.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "external_links",
        uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id", "platform"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ExternalLinkJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileJpaEntity profile;

    @Column(name = "platform", nullable = false, length = 50)
    private String platform;

    @Column(name = "url", nullable = false)
    private String url;
}