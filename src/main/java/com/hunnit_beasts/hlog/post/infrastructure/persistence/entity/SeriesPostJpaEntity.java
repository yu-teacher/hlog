package com.hunnit_beasts.hlog.post.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "series_posts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"series_id", "post_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeriesPostJpaEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "series_id", nullable = false)
    private UUID seriesId;

    @Column(name = "post_id", nullable = false)
    private UUID postId;

    @Column(name = "post_order", nullable = false)
    private Integer order;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}