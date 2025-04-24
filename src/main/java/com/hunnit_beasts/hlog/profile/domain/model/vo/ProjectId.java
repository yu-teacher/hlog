package com.hunnit_beasts.hlog.profile.domain.model.vo;

import lombok.Getter;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ProjectId {
    private final UUID value;

    private ProjectId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static ProjectId create() {
        return new ProjectId(UUID.randomUUID());
    }

    public static ProjectId of(UUID value) {
        return new ProjectId(value);
    }

    public static ProjectId of(String value) {
        return new ProjectId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectId projectId = (ProjectId) o;
        return value.equals(projectId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}