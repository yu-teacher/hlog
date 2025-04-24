package com.hunnit_beasts.hlog.post.domain.model.vo;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class SeriesId {
    private final UUID value;

    private SeriesId(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static SeriesId create() {
        return new SeriesId(UUID.randomUUID());
    }

    public static SeriesId of(UUID value) {
        return new SeriesId(value);
    }

    public static SeriesId of(String value) {
        return new SeriesId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeriesId seriesId = (SeriesId) o;
        return value.equals(seriesId.value);
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