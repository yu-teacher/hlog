package com.hunnit_beasts.hlog.post.domain.model.vo;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class SeriesPostId {
    private final UUID value;

    private SeriesPostId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static SeriesPostId create() {
        return new SeriesPostId(UUID.randomUUID());
    }

    public static SeriesPostId of(UUID value) {
        return new SeriesPostId(value);
    }

    public static SeriesPostId of(String value) {
        return new SeriesPostId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeriesPostId that = (SeriesPostId) o;
        return value.equals(that.value);
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