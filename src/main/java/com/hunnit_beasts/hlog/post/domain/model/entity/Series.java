package com.hunnit_beasts.hlog.post.domain.model.entity;

import com.hunnit_beasts.hlog.post.domain.model.vo.Description;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesId;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesName;
import com.hunnit_beasts.hlog.post.domain.model.vo.SeriesStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Series {
    private final SeriesId id;
    private SeriesName name;
    private Description description;
    private final UUID authorId;
    private SeriesStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Series(SeriesId id, SeriesName name, Description description,
                   UUID authorId, SeriesStatus status, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = description; // 설명은 null 가능
        this.authorId = Objects.requireNonNull(authorId);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = createdAt;
    }

    public static Series create(SeriesName name, Description description, UUID authorId) {
        return new Series(
                SeriesId.create(),
                name,
                description,
                authorId,
                SeriesStatus.ACTIVE,
                LocalDateTime.now()
        );
    }

    public static Series reconstitute(
            SeriesId id, SeriesName name, Description description,
            UUID authorId, SeriesStatus status,
            LocalDateTime createdAt, LocalDateTime updatedAt) {

        Series series = new Series(id, name, description, authorId, status, createdAt);
        series.updatedAt = updatedAt;

        return series;
    }

    public void updateName(SeriesName name) {
        this.name = Objects.requireNonNull(name);
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDescription(Description description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = SeriesStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = SeriesStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
}