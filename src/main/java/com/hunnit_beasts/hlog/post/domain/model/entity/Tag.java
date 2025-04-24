package com.hunnit_beasts.hlog.post.domain.model.entity;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Tag {
    private final String name;
    private final LocalDateTime createdAt;
    private int usageCount;

    private Tag(String name, LocalDateTime createdAt) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Tag name cannot exceed 50 characters");
        }
        this.name = name.toLowerCase().trim();
        this.createdAt = Objects.requireNonNull(createdAt);
        this.usageCount = 0;
    }

    public static Tag create(String name) {
        return new Tag(name, LocalDateTime.now());
    }

    public static Tag reconstitute(String name, LocalDateTime createdAt, int usageCount) {
        Tag tag = new Tag(name, createdAt);
        tag.usageCount = usageCount;
        return tag;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public void decrementUsage() {
        this.usageCount = Math.max(0, this.usageCount - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
