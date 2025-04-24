package com.hunnit_beasts.hlog.post.domain.model.vo;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Description {
    private final String value;

    private Description(String value) {
        if (value != null && value.length() > 500) {
            throw new IllegalArgumentException("Description cannot exceed 500 characters");
        }
        this.value = value;
    }

    public static Description of(String value) {
        return new Description(value);
    }

    public static Description empty() {
        return new Description("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Description that = (Description) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value : "";
    }
}