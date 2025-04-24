package com.hunnit_beasts.hlog.post.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TagJpaEntity {

    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "usage_count", nullable = false)
    private int usageCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
