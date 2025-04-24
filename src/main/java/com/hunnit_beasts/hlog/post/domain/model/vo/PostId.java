package com.hunnit_beasts.hlog.post.domain.model.vo;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class PostId {
    private final UUID value;

    private PostId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static PostId create() {
        return new PostId(UUID.randomUUID());
    }

    public static PostId of(UUID value) {
        return new PostId(value);
    }

    public static PostId of(String value) {
        return new PostId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostId postId = (PostId) o;
        return value.equals(postId.value);
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