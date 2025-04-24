package com.hunnit_beasts.hlog.post.domain.model.vo;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Title {
    private final String value;

    private Title(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (value.length() > 200) {
            throw new IllegalArgumentException("Title cannot exceed 200 characters");
        }
        this.value = value;
    }

    public static Title of(String value) {
        return new Title(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Title title = (Title) o;
        return value.equals(title.value);
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
