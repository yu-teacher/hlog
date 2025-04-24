package com.hunnit_beasts.hlog.post.domain.model.vo;

import lombok.Getter;

import java.util.Objects;

@Getter
public class SeriesName {
    private final String value;

    private SeriesName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Series name cannot be empty");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Series name cannot exceed 100 characters");
        }
        this.value = value;
    }

    public static SeriesName of(String value) {
        return new SeriesName(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeriesName that = (SeriesName) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}