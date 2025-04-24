package com.hunnit_beasts.hlog.comment.domain.model.vo;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class CommentId {
    private final UUID value;

    private CommentId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static CommentId create() {
        return new CommentId(UUID.randomUUID());
    }

    public static CommentId of(UUID value) {
        return new CommentId(value);
    }

    public static CommentId of(String value) {
        return new CommentId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentId commentId = (CommentId) o;
        return value.equals(commentId.value);
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